package com.vaddemgen.intelligenthouseclient.service.sink;

import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public final class Bme280EventLogSink implements Bme280EventSink {

  @Override
  public void sendEvent(short sensorId, Instant occurredAt, Bme280Value value) {
    log.info(
        "Bme280[{}] {}, {}, {}, {}",
        sensorId,
        String.format("%.2f°C", value.getCelsiusTemp()),
        String.format("%.2f°F", value.getFahrenheitTemp()),
        String.format("%.2fhPa", value.getPressure()),
        String.format("%.2f%% RH", value.getHumidity())
    );
  }
}
