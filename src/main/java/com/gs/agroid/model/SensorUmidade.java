package com.gs.agroid.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("UMIDADE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SensorUmidade extends Sensor {

    @Builder
    public SensorUmidade(Long id, String modelo, String status, Propriedade propriedade) {
        super(id, modelo, status, propriedade);
    }

    @Override
    public String getTipoSensor() {
        return "UMIDADE";
    }
}
