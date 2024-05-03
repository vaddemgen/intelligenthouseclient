package com.vaddemgen.intelligenthouseclient.service.sink;

import java.time.Instant;

public interface EventSink<T> {

  void sendEvent(short sensorId, Instant occurredAt, T value);
}
