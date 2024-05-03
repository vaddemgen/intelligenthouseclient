package com.vaddemgen.intelligenthouseclient.repository;

import com.vaddemgen.intelligenthouseclient.model.entity.Bme280EventEntity;
import com.vaddemgen.intelligenthouseclient.model.entity.QueueStatistic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface Bme280EventQueueRepository extends CrudRepository<Bme280EventEntity, Long> {

  @Query(value = "select count(1) as size, min(e.occurredAt) as first, max(e.occurredAt) as last from Bme280EventEntity e")
  QueueStatistic getQueueStatistic();
}
