package com.vaddemgen.intelligenthouseclient.socket;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaddemgen.intelligenthouseclient.bme280.Bme280Service;
import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class Bme280Channel implements Runnable {

  public static final Gson gson = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .create();

  private final transient Socket client;
  private final transient Bme280Service bme280Service;

  Bme280Channel(Socket client, Bme280Service bme280Service) {
    this.client = client;
    this.bme280Service = bme280Service;
  }

  private static PrintWriter createPrintWriter(Socket client) {
    try {
      return new PrintWriter(new BufferedWriter(new OutputStreamWriter(
          client.getOutputStream(), StandardCharsets.UTF_8)), true);
    } catch (IOException e) {
      log.info("Can't obtain the output stream from the socket client", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    // Creating the intermediate OutputStreamWriter,
    // which converts characters into bytes using the UTF_8 encoding.
    try (PrintWriter writer = createPrintWriter(client)) {
      // This list has only one item and is designed to access to the item
      // from the lambda expression.
      LinkedList<Consumer<Bme280Value>> list = new LinkedList<>();

      list.add(value -> {
        if (!client.isClosed() && !writer.checkError()) {
          writer.println(gson.toJson(value));
        } else {
          log.info("BME280_CHANNEL: The client '{}' was closed",
              client.getRemoteSocketAddress());

          bme280Service.unsubscribe(list.getFirst());
          try {
            client.close();
          } catch (IOException e) {
            log.info(e.getMessage(), e);
          }
        }
      });

      bme280Service.subscribe(list.getFirst());
    }
  }
}
