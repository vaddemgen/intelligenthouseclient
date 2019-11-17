package com.vaddemgen.intelligenthouseclient;

import com.vaddemgen.intelligenthouseclient.socket.IntelligentHouseClientSocketServer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Application {

  /**
   * Application entry point.
   */
  public static void main(String[] args) {

    try {
      IntelligentHouseClientSocketServer.start(7007);

      log.info("Enter 'q' to exit");

      //noinspection StatementWithEmptyBody
      while (System.in.read() != 'q') {
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);

      try {
        IntelligentHouseClientSocketServer.shutdownAndAwaitTermination();
      } catch (IOException e2) {
        log.error(e2.getMessage(), e2);
      }

      System.exit(-1);
    }
  }
}
