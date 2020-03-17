package com.vaddemgen.intelligenthouseclient;

import com.vaddemgen.intelligenthouseclient.socket.IntelligentHouseClientSocketServer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Application {

  /**
   * Application entry point.
   */
  public static void main(String[] args) {

    try (Scanner in = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
      IntelligentHouseClientSocketServer.start(7007);

      do {
        log.info("Enter 'q' to exit");
      } while (!in.nextLine().equals("q"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);

      try {
        IntelligentHouseClientSocketServer.shutdownAndAwaitTermination();
      } catch (IOException e2) {
        log.error(e2.getMessage(), e2);
      }
    }
  }
}
