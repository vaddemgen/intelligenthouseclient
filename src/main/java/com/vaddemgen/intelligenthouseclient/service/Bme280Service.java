package com.vaddemgen.intelligenthouseclient.service;

import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import lombok.SneakyThrows;

public interface Bme280Service {

  Bme280Value readBme280Value();
}
