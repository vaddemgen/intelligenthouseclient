package com.vaddemgen.intelligenthouseclient.kafka;

import com.google.gson.Gson;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaChannel implements Closeable {

  private static final Gson GSON = new Gson();

  private final transient KafkaProducer<String, String> producer;

  private final transient String topic;

  /**
   * Inits Kafka channel.
   */
  public KafkaChannel(Map<String, String> options) {
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        options.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class.getName());
    producer = new KafkaProducer<>(properties);
    topic = options.get("topic");
  }

  public void accept(Bme280Value value) {
    producer.send(new ProducerRecord<>(topic, GSON.toJson(value)));
    producer.flush();
  }

  @Override
  public void close() {
    producer.close();
  }
}
