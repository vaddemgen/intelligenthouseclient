package com.vaddemgen.intelligenthouseclient.service.sink;

import com.vaddemgen.intelligenthouseclient.bme280.util.Bme280Value;
import com.vaddemgen.intelligenthouseclient.model.entity.Bme280EventEntity;
import com.vaddemgen.intelligenthouseclient.model.entity.QueueStatistic;
import com.vaddemgen.intelligenthouseclient.repository.Bme280EventQueueRepository;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
class Bme280EventKafkaSink implements Bme280EventSink {

  private final Bme280EventQueueRepository queueRepository;

  @PostConstruct
  public void init() {
    QueueStatistic stat = queueRepository.getQueueStatistic();

    if (stat.getSize() > 0) {
      log.info("Bme280EventQueue:\n- Events count: {}\n- First: {}\n- Last: {}\n- Queue Duration: {}",
          stat.getSize(),
          stat.getFirst(),
          stat.getLast(),
          Duration.between(stat.getFirst(), stat.getLast())
      );
    }
  }

  @Override
  public void sendEvent(short sensorId, Instant occurredAt, Bme280Value value) {
    queueRepository.save(
        Bme280EventEntity.builder()
            .sensorId(sensorId)
            .celsiusTemp(value.getCelsiusTemp())
            .fahrenheitTemp(value.getFahrenheitTemp())
            .pressure(value.getPressure())
            .humidity(value.getHumidity())
            .occurredAt(occurredAt)
            .build()
    );
    log.debug("Bme280[{}] Event {} saved to DB", sensorId, value);
  }
}
