package com.vaddemgen.intelligenthouseclient.socket;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaddemgen.intelligenthouseclient.bme280.Bme280Service;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Bme280Channel implements Callable<Void> {

  private final static Logger LOGGER = LoggerFactory.getLogger(Bme280Channel.class);

  private Socket client;
  private Bme280Service bme280Service;

  Bme280Channel(Socket client, Bme280Service bme280Service) {
    this.client = client;
    this.bme280Service = bme280Service;
  }

  @Override
  public Void call() throws IOException {
    PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

    Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();

    LinkedList<Consumer<Bme280Value>> list = new LinkedList<>();

    list.add(value -> {
      if (!client.isClosed() && !writer.checkError()) {
        writer.println(gson.toJson(value));
      } else {
        LOGGER.info("BME280_CHANNEL: The client '{}' was closed",
            client.getRemoteSocketAddress());

        bme280Service.unsubscribe(list.getFirst());
        try {
          writer.close();
          client.close();
        } catch (IOException e) {
          LOGGER.info(e.getMessage(), e);
        }
      }
    });

    bme280Service.subscribe(list.getFirst());

    return null;
  }
}
