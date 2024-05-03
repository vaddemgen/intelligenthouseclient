# BME280

## Install

### Install Libraries
- https://pi4j.com/about/download/ (Try installing V2, but tested with v1)
- https://github.com/WiringPi/WiringPi?tab=readme-ov-file#from-source From Source

### Create a Kafka Topic
```bash
$KAFKA_HOME/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --topic iot_bme280 \
  --partitions 3 \
  --config cleanup.policy=delete \
  --config retention.ms=2592000000 \
  --create
```

## Start the Dependencies:

- `$HADOOP_HOME/sbin/start-dfs.sh`
- `$SPARK_HOME/sbin/start-all.sh`
- `sudo systemctl start kafka`

## Monitor

```bash
$KAFKA_HOME/bin/kafka-console-consumer.sh --topic iot_bme280 --bootstrap-server localhost:9092 --from-beginning
```

http://192.168.50.167:4040/StreamingQuery/

## Start the Client:

```bash
java -jar intelligenthouseclient-1.0.0-alpha.3-all.jar R 1 120 localhost:9092 iot_bme280
```

```bash
nohup java -jar intelligenthouseclient-1.0.0-alpha.3-all.jar R 1 120 localhost:9092 iot_bme280 &
```

## Future

```bash
spark-shell --packages io.delta:delta-spark_2.13:3.1.0,org.apache.spark:spark-sql-kafka-0-10_2.13:3.5.1\
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension"\
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog"
```

```scala
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType

spark.readStream
    .format("kafka")
    .options(Map(
        "startingOffsets" -> "earliest",
        "kafka.bootstrap.servers" -> "localhost:9092",
        "failOnDataLoss" -> "false",
        "subscribe" -> "iot_bme280"
    ))
    .load()
    .withColumn("date", date_format($"timestamp", "yyyy-MM-dd"))
    .writeStream
    .queryName("iot_bronze")
    .format("delta")
    .option("checkpointLocation", "hdfs://localhost:9000/public/iot/checkpoint/iot_bronze")
    .option("spark.sql.shuffle.partitions", 3)
    .option("spark.databricks.delta.autoCompact.enabled", "true")
    .option("spark.databricks.delta.optimizeWrite.enabled", "true")
    .option("delta.autoOptimize.optimizeWrite", "true")
    .option("delta.autoOptimize.autoCompact", "true")
    .partitionBy("topic", "date")
    .trigger(Trigger.ProcessingTime("2 minutes"))
    .start("hdfs://localhost:9000/public/iot/iot_bronze")

spark.readStream
    .format("delta")
    .load("hdfs://localhost:9000/public/iot/iot_bronze")
    .where(col("topic") === "iot_bme280")
    .withColumn("hour", hour($"timestamp"))
    .withColumn("key", col("key").cast("string"))
    .withColumn("value", from_json(col("value").cast("string"), StructType.fromDDL("celsiusTemp FLOAT, fahrenheitTemp FLOAT, pressure FLOAT, humidity FLOAT")))
    .writeStream
    .queryName("iot_bme280_silver")
    .format("delta")
    .option("checkpointLocation", "hdfs://localhost:9000/public/iot/checkpoint/iot_bme280")
    .option("spark.sql.shuffle.partitions", 3)
    .option("spark.databricks.delta.autoCompact.enabled", "true")
    .option("spark.databricks.delta.optimizeWrite.enabled", "true")
    .option("delta.autoOptimize.optimizeWrite", "true")
    .option("delta.autoOptimize.autoCompact", "true")
    .partitionBy("date", "hour")
    .trigger(Trigger.ProcessingTime("2 minutes"))
    .start("hdfs://localhost:9000/public/iot/iot_bme280_silver")
```

```scala
spark.table("DELTA.`hdfs://localhost:9000/public/iot/iot_bme280_silver`").show()
```