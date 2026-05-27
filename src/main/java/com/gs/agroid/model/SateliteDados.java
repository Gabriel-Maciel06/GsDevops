package com.gs.agroid.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_SATELITE_DADOS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SateliteDados {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_satelite")
    @SequenceGenerator(name = "seq_satelite", sequenceName = "SEQ_SATELITE", allocationSize = 1)
    @Column(name = "id_satelite")
    private Long id;

    @Column(name = "umidade_prevista", nullable = false, precision = 5, scale = 2)
    private BigDecimal umidadePrevista;

    @Column(name = "clima", nullable = false, length = 50)
    private String clima;

    @Column(name = "regiao", nullable = false, length = 100)
    private String regiao;

    @Column(name = "data_coleta", nullable = false)
    private LocalDateTime timestamp;
}
