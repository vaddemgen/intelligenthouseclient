package com.vaddemgen.intelligenthouseclient;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.PlatformAlreadyAssignedException;
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
        } catch (IOException | UnsupportedBusNumberException | PlatformAlreadyAssignedException e) {
            LOGGER.error(e.getMessage(), e);

            try {
                IntelligentHouseClientSocketServer.stop();
            } catch (IOException e2) {
                LOGGER.error(e2.getMessage(), e2);
            }

            System.exit(-1);
        }
    }

}
