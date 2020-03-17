package com.vaddemgen.intelligenthouseclient.bme280;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.platform.PlatformManager;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Utils;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.io.IOException;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Bme280Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Bme280Service.class);
  private static final Duration WAITING_TIME = Duration.ofMinutes(1);


  private transient I2CDevice i2CDevice;
  private transient ExecutorService executorService = Executors.newSingleThreadExecutor();

  private transient Queue<Consumer<Bme280Value>> subscribers = new ArrayBlockingQueue<>(10);

  /**
   * Don't let anyone to instantiate this class.
   */
  private Bme280Service()
      throws PlatformAlreadyAssignedException, UnsupportedBusNumberException, IOException {
    PlatformManager.setPlatform(Platform.BANANAPI);

    // Create I2C bus
    I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_2);
    // Get I2C device, BME280 I2C address is 0x76(108)
    i2CDevice = bus.getDevice(0x76);
  }

  public static Bme280Service startService()
      throws UnsupportedBusNumberException, IOException, PlatformAlreadyAssignedException {
    return new Bme280Service().launch();
  }

  private static Bme280Value readBme280Value(I2CDevice device) throws IOException {
    return Bme280Utils.readValue(device);
  }

  public void subscribe(Consumer<Bme280Value> subscriber) {
    subscribers.add(subscriber);
  }

  private Bme280Service launch() {
    executorService.execute(() -> {
      //noinspection InfiniteLoopStatement
      while (true) {
        if (!subscribers.isEmpty()) {
          try {
            Bme280Value bme280Value = readBme280Value(i2CDevice);
            LOGGER.trace("BME280_SERVICE: {}", bme280Value);
            subscribers.forEach(s -> s.accept(bme280Value));
          } catch (IOException e) {
            LOGGER.error("BME280_SERVICE: Failed to read value", e);
          }
        } else {
          LOGGER.info("BME280_SERVICE: No subscribers");
        }
        try {
          Thread.sleep(WAITING_TIME.toMillis());
        } catch (InterruptedException e) {
          LOGGER.trace("BME280_SERVICE: Interrupted.");
        }
      }
    });

    return this;
  }

  /**
   * Shutdowns the service.
   */
  public void shutdownServiceAndAwaitTermination() {
    executorService.shutdown();
    LOGGER.warn("BME280_SERVICE: "
        + "Awaiting service termination up to 2 minutes.");
    try {
      if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
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
        LOGGER.warn("BME280_SERVICE: "
            + "The executor service didn't terminate.");
      } else {
        LOGGER.info("BME280_SERVICE: Stopped.");
      }
    } catch (InterruptedException e) {
      subscribers.clear();
      LOGGER.warn("BME280_SERVICE: "
          + "The executor service didn't terminate.", e);
      Thread.currentThread().interrupt();
    }
    subscribers.clear();
  }

  public void unsubscribe(Consumer<Bme280Value> subscriber) {
    subscribers.remove(subscriber);
  }
}
