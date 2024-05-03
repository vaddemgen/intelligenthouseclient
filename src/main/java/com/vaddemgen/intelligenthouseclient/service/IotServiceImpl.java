package com.vaddemgen.intelligenthouseclient.service;

import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import com.vaddemgen.intelligenthouseclient.service.sink.EventDeliveryService;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IotServiceImpl implements IotService {

  private static final short BME_280_SENSOR_ID = 1;

  private final Bme280Service bme280Service;
  private final EventDeliveryService deliveryService;

  @Scheduled(fixedDelayString = "${app.bme280.updateFrequency}")
  public void bme280Beat() {
    Bme280Value event = bme280Service.readBme280Value();
    deliveryService.sendEvent(BME_280_SENSOR_ID, Instant.now(), event);
  }
}
