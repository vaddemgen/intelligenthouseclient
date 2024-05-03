package com.vaddemgen.intelligenthouseclient.service.sink;

import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.time.Instant;

public interface EventDeliveryService {

  void sendEvent(short sensorId, Instant occurredAt, Bme280Value value);
}
