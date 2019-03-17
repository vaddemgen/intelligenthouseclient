package com.vaddemgen.intelligenthouseclient.socket;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.vaddemgen.intelligenthouseclient.bme280.Bme280Service;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IntelligentHouseClientSocketServer {

    private final static Logger LOGGER = LoggerFactory
        .getLogger(IntelligentHouseClientSocketServer.class);

    private static ServerSocket serverSocket;
    private static ExecutorService executorService;
    private static Bme280Service bme280Service;

    /**
     * Don't let anyone to instantiate this class.
     */
    private IntelligentHouseClientSocketServer() {
    }

    public static void start(int port)
        throws IOException, UnsupportedBusNumberException, PlatformAlreadyAssignedException {
        bme280Service = Bme280Service.startService();
        serverSocket = new ServerSocket(port);
        executorService = Executors.newCachedThreadPool();

        LOGGER.info("SOCKET_SERVER: The socket server started at port {}", port);

        executorService.submit(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();

                LOGGER.info("SOCKET_SERVER: Caught a client {}",
                    clientSocket.getRemoteSocketAddress());

                executorService.submit(new Bme280Channel(clientSocket, bme280Service));
            }
        });
    }

    public static void stop() throws IOException {

        if (executorService != null) {
            executorService.shutdownNow();
            LOGGER.info("SOCKET_SERVER: executor service stopped");
        }
        if (serverSocket != null) {
            serverSocket.close();
            LOGGER.info("SOCKET_SERVER: sockets was closed");
        }
        if (bme280Service != null) {
            bme280Service.stopService();
            LOGGER.info("SOCKET_SERVER: Stopped BME280 Service");
        }
    }
}
