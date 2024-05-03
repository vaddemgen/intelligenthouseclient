package com.vaddemgen.intelligenthouseclient.service;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Utils;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import com.vaddemgen.intelligenthouseclient.model.AppConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class Bme280ServiceImpl implements Bme280Service {

  private final I2CDevice i2CDevice;

  @SneakyThrows
  public Bme280ServiceImpl(AppConfig conf) {
    // Create I2C bus
    I2CBus bus = I2CFactory.getInstance(conf.getBme280().getBus());
    // Get I2C device, BME280 I2C address is 0x76(108)
    i2CDevice = bus.getDevice(0x76);
  }

  @Override
  @SneakyThrows
  public Bme280Value readBme280Value() {
    return Bme280Utils.readValue(i2CDevice);
  }
}
