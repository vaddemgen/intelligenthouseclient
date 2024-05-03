package com.vaddemgen.intelligenthouseclient.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bme280_event_queue")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bme280EventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column
  private short sensorId;

  @Column
  private float celsiusTemp;

  @Column
  private float fahrenheitTemp;

  @Column
  private float pressure;

  @Column
  private float humidity;

  @Column
  private Instant occurredAt;
}
