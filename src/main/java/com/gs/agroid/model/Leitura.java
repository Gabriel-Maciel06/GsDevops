package com.gs.agroid.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "TB_LEITURA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leitura {

    @EmbeddedId
    private LeituraId id;

    @MapsId("sensorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sensor", insertable = false, updatable = false)
    private Sensor sensor;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}
