package com.vaddemgen.intelligenthouseclient;

import com.vaddemgen.intelligenthouseclient.bme280.Bme280Service;
import com.vaddemgen.intelligenthouseclient.bme280.PlatformOptions;
import com.vaddemgen.intelligenthouseclient.kafka.KafkaChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Application {

  public static final Duration BACKGROUND_SLEEP_DURATION = Duration.ofSeconds(5);

  /**
   * Application entry point.
   */
  public static void main(String[] args) {

    log.info("Program parameters {}", Arrays.asList(args));

    PlatformOptions options = new PlatformOptions(args);
    KafkaOptions kafkaOptions = new KafkaOptions(args);

    log.info("Parameters {}\n{}", options, kafkaOptions);

    try (Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
        KafkaChannel channel = new KafkaChannel(kafkaOptions.getOptions());
        Bme280Service bme280Service = new Bme280Service(options)) {
      bme280Service.subscribe(value -> log.info(
          "\nBME 280 DATA:\n"
              + " - Temperature in Celsius : {},\n"
              + " - Temperature in Fahrenheit : {},\n"
              + " - Pressure : {},\n"
              + " - Relative Humidity : {}",
          String.format("%.2f C", value.getCelsiusTemp()),
          String.format("%.2f F", value.getFahrenheitTemp()),
          String.format("%.2f hPa", value.getPressure()),
          String.format("%.2f %% RH", value.getHumidity())
      ));
      bme280Service.subscribe(channel::accept);
      bme280Service.launch();

      hangApp(in);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
  @SneakyThrows
  private static void hangApp(Scanner in) {
    try {
      do {
        log.info("Enter 'q' to exit");
      } while (!in.nextLine().equals("q"));
    } catch (NoSuchElementException ignored) {
      log.info("The application was run in background hence hanging the current thread");

      while (true) {
        Thread.sleep(BACKGROUND_SLEEP_DURATION.toMillis());
      }
    }
  }

  @ToString(doNotUseGetters = true)
  private static class KafkaOptions {

    private final transient String topic;
    private final transient String kafkaBootstrapServers;

    KafkaOptions(String[] args) {
      kafkaBootstrapServers = args.length >= 4 ? args[3] : "192.168.50.167:9092";
      topic = args.length >= 5 ? args[4] : "test";
    }

    Map<String, String> getOptions() {
      return Map.of(
          "topic", topic,
          "bootstrap.servers", kafkaBootstrapServers
      );
    }
  }
}