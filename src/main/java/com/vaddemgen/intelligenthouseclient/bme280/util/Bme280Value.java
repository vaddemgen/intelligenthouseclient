package com.vaddemgen.intelligenthouseclient.bme280.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Bme280Value {

  private final double celsiusTemp;
  private final double fahrenheitTemp;
  private final double pressure;
  private final double humidity;

  @Override
  public String toString() {

    // Output data to screen
    return String.format("Temperature in Celsius : %.2f C, ", celsiusTemp)
        + String.format("Temperature in Fahrenheit : %.2f F, ", fahrenheitTemp)
        + String.format("Pressure : %.2f hPa, ", pressure)
        + String.format("Relative Humidity : %.2f %% RH", humidity);
  }
}
