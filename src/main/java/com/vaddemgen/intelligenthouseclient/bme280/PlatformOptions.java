package com.vaddemgen.intelligenthouseclient.bme280;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.platform.Platform;
import java.time.Duration;
import java.util.Locale;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class PlatformOptions {

  private final String platform;

  private final int bus;

  private final Duration frequency;

  /**
   * Creates PlatformOptions.
   */
  public PlatformOptions(String[] args) {
    platform = args[0];
    bus = Integer.parseInt(args[1]);
    frequency =
        args.length >= 3 ? Duration.ofSeconds(Integer.parseInt(args[2])) : Duration.ofMinutes(2);
  }

  public Platform getPlatform() {
    return platform.toUpperCase(Locale.getDefault()).startsWith("R") ? Platform.RASPBERRYPI
        : Platform.SIMULATED;
  }

  /**
   * Gets bus number.
   */
  public int getBus() {
    switch (bus) {
      case 0:
        return I2CBus.BUS_0;
      case 1:
        return I2CBus.BUS_1;
      case 2:
        return I2CBus.BUS_2;
      case 3:
        return I2CBus.BUS_3;
      case 4:
        return I2CBus.BUS_4;
      default:
        return I2CBus.BUS_5;
    }
  }
}


