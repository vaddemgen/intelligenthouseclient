package com.vaddemgen.intelligenthouseclient.model.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface QueueStatistic {

  long getSize();

  Instant getFirst();

  Instant getLast();
}
