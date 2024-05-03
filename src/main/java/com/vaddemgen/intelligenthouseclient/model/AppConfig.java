package com.vaddemgen.intelligenthouseclient.model;

import com.pi4j.platform.Platform;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {

  private Platform platform;
  private Bme280Config bme280;

  @Component
  @Getter
  @Setter
  public static class Bme280Config {

    private int bus;
    private int address;
    private Duration updateFrequency;
  }
}

