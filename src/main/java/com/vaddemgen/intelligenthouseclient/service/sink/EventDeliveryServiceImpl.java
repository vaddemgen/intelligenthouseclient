package com.vaddemgen.intelligenthouseclient.service.sink;

import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
final class EventDeliveryServiceImpl implements EventDeliveryService {

  private final List<Bme280EventSink> sinks;

  public EventDeliveryServiceImpl(Bme280EventLogSink bme280EventLogSink, Bme280EventKafkaSink bme280EventKafkaSink) {
    sinks = List.of(bme280EventLogSink, bme280EventKafkaSink);
  }

  @Override
  public void sendEvent(short sensorId, Instant occurredAt, Bme280Value value) {
    sinks.forEach(sink -> sink.sendEvent(sensorId, occurredAt, value));
  }
}
