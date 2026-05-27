package com.gs.agroid.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_SENSOR")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_sensor", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sensor")
    @SequenceGenerator(name = "seq_sensor", sequenceName = "SEQ_SENSOR", allocationSize = 1)
    @Column(name = "id_sensor")
    private Long id;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // ATIVO, INATIVO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propriedade", nullable = false)
    private Propriedade propriedade;

    @Transient
    public abstract String getTipoSensor();
}
