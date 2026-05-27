package com.gs.agroid.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeituraId implements Serializable {

    @Column(name = "id_sensor")
    private Long sensorId;

    @Column(name = "data_leitura")
    private LocalDateTime timestamp;
}
