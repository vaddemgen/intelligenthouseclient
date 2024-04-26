package com.vaddemgen.intelligenthouseclient.bme280;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Utils;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Bme280Service implements Closeable {

  private static final Duration AWAIT_SOFT_TERMINATION = Duration.ofSeconds(10);

  private final transient I2CDevice i2CDevice;
  private final transient ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final transient Queue<Consumer<Bme280Value>> subscribers = new ArrayBlockingQueue<>(10);
  @Getter
  @Setter
  private transient Duration waitingTime = Duration.ofSeconds(5);

  /**
   * Don't let anyone instantiate this class.
   */
  public Bme280Service(PlatformOptions options)
      throws PlatformAlreadyAssignedException, UnsupportedBusNumberException, IOException {
    PlatformManager.setPlatform(options.getPlatform());

    // Create I2C bus
    I2CBus bus = I2CFactory.getInstance(options.getBus());
    // Get I2C device, BME280 I2C address is 0x76(108)
    i2CDevice = bus.getDevice(0x76);

    setWaitingTime(options.getFrequency());
  }

  private static Bme280Value readBme280Value(I2CDevice device) throws IOException {
    return Bme280Utils.readValue(device);
  }

  public void subscribe(Consumer<Bme280Value> subscriber) {
    subscribers.add(subscriber);
  }

  /**
   * Launches the service.
   */
  public Bme280Service launch() {
    executorService.execute(() -> {
      while (true) {
        if (!subscribers.isEmpty()) {
          try {
            Bme280Value bme280Value = readBme280Value(i2CDevice);
            log.trace("BME280_SERVICE: {}", bme280Value);
            subscribers.forEach(s -> s.accept(bme280Value));
          } catch (IOException e) {
            log.error("BME280_SERVICE: Failed to read value", e);
          }
        } else {
          log.info("BME280_SERVICE: No subscribers");
        }
        try {
          Thread.sleep(waitingTime.toMillis());
        } catch (InterruptedException e) {
          log.trace("BME280_SERVICE: Interrupted.");
          return;
        }
      }
    });

    return this;
  }

  /**
   * Shutdowns the service.
   */
  private void shutdownServiceAndAwaitTermination() {
    executorService.shutdown();
    log.warn("BME280_SERVICE: "
        + "Awaiting service termination up to {}.", AWAIT_SOFT_TERMINATION);
    try {
      if (!executorService.awaitTermination(AWAIT_SOFT_TERMINATION.getSeconds(),
          TimeUnit.SECONDS)) {
        shutdownNowServiceAndAwaitTermination();
      }
    } catch (InterruptedException e) {
      shutdownNowServiceAndAwaitTermination();
      Thread.currentThread().interrupt();
    }
  }

  private void shutdownNowServiceAndAwaitTermination() {
    executorService.shutdownNow();
    try {
      if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
        log.warn("BME280_SERVICE: "
            + "The executor service hasn't been terminated.");
      } else {
        log.info("BME280_SERVICE: Stopped.");
      }
    } catch (InterruptedException e) {
      subscribers.clear();
      log.warn("BME280_SERVICE: "
          + "The executor service didn't terminate.", e);
      Thread.currentThread().interrupt();
    }
    subscribers.clear();
  }

  public void unsubscribe(Consumer<Bme280Value> subscriber) {
    subscribers.remove(subscriber);
  }

  @Override
  public void close() {
    shutdownServiceAndAwaitTermination();
  }
}
