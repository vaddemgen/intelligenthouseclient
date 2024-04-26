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

## Check Data

```bash
$KAFKA_HOME/bin/kafka-console-consumer.sh --topic iot_bme280 --bootstrap-server localhost:9092 --from-beginning
```

## Start the Client:

```bash
java -jar intelligenthouseclient-1.0.0-alpha.3-all.jar R 1 120 localhost:9092 iot_bme280
```

```bash
nohup java -jar intelligenthouseclient-1.0.0-alpha.3-all.jar R 1 120 localhost:9092 iot_bme280 &
```

## Future

```bash
spark-shell --packages io.delta:delta-spark_2.13:3.1.0\
    --conf "spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension"\
    --conf "spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog"
```