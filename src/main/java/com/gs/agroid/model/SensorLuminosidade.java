package com.gs.agroid.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("LUMINOSIDADE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SensorLuminosidade extends Sensor {

    @Builder
    public SensorLuminosidade(Long id, String modelo, String status, Propriedade propriedade) {
        super(id, modelo, status, propriedade);
    }

    @Override
    public String getTipoSensor() {
        return "LUMINOSIDADE";
    }
}
