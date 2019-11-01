package com.vaddemgen.intelligenthouseclient;

import com.vaddemgen.intelligenthouseclient.socket.IntelligentHouseClientSocketServer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application {

  private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {

    try {
      IntelligentHouseClientSocketServer.start(7007);

      LOGGER.info("Enter 'q' to exit");

      //noinspection StatementWithEmptyBody
      while (System.in.read() != 'q') {
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);

      try {
        IntelligentHouseClientSocketServer.shutdownAndAwaitTermination();
      } catch (IOException e2) {
        LOGGER.error(e2.getMessage(), e2);
      }

      System.exit(-1);
    }
  }

}
