package com.vaddemgen.intelligenthouseclient.socket;

import static java.util.Objects.nonNull;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.vaddemgen.intelligenthouseclient.bme280.Bme280Service;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class IntelligentHouseClientSocketServer {

  private static ServerSocket serverSocket;
  private static ExecutorService executorService;
  private static Bme280Service bme280Service;

  /**
   * Don't let anyone to instantiate this class.
   */
  private IntelligentHouseClientSocketServer() {
  }

  /**
   * Starts the client at the provided {@code port}.
   */
  public static void start(int port)
      throws IOException, UnsupportedBusNumberException, PlatformAlreadyAssignedException {
    bme280Service = Bme280Service.startService();
    serverSocket = new ServerSocket(port);
    executorService = Executors.newCachedThreadPool();

    log.info("SOCKET_SERVER: The socket server started at port {}", port);

    initBme280Logger(bme280Service);

    executorService.submit(() -> {
      try {
        //noinspection InfiniteLoopStatement
        while (true) {
          Socket clientSocket = serverSocket.accept();

          log.info("SOCKET_SERVER: Caught a client {}",
              clientSocket.getRemoteSocketAddress());

          executorService.submit(new Bme280Channel(clientSocket, bme280Service));
        }
      } catch (Exception e) {
        log.error("SOCKET_SERVER: Caught an exception.", e);
        executorService.shutdown();
      }
    });
  }

  private static void initBme280Logger(Bme280Service bme280Service) {
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
  }

  /**
   * Shutdowns the client.
   */
  public static void shutdownAndAwaitTermination() throws IOException {
    shutdownExecutorService();
    if (nonNull(serverSocket)) {
      serverSocket.close();
      log.info("SOCKET_SERVER: sockets was closed.");
    }
    if (nonNull(bme280Service)) {
      bme280Service.shutdownServiceAndAwaitTermination();
      log.info("SOCKET_SERVER: Stopped BME280 Service.");
    }
  }

  private static void shutdownExecutorService() {
    if (nonNull(executorService)) {
      executorService.shutdown();
      log.warn("SOCKET_SERVER: "
          + "Awaiting service termination up to 2 minutes.");
      try {
        if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
          shutdownNowExecutorService();
        }
      } catch (InterruptedException e) {
        shutdownNowExecutorService();
        Thread.currentThread().interrupt();
      }
    }
  }

  private static void shutdownNowExecutorService() {
    executorService.shutdownNow();
    try {
      if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
        log.warn("SOCKET_SERVER: "
            + "The executor service didn't terminate.");
      } else {
        log.info("SOCKET_SERVER: executor service stopped.");
      }
    } catch (InterruptedException e) {
      log.warn("SOCKET_SERVER: "
          + "The executor service didn't terminate.", e);
      Thread.currentThread().interrupt();
    }
  }
}
