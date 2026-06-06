package com.gs.agroid.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_ATUADOR")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Atuador {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_atuador")
    @SequenceGenerator(name = "seq_atuador", sequenceName = "SEQ_ATUADOR", allocationSize = 1)
    @Column(name = "id_atuador")
    private Long id;

    @Column(name = "tipo_atuador", nullable = false, length = 50)
    private String tipoAtuador;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "estado_atual", nullable = false, length = 20)
    private String estadoAtual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propriedade", nullable = false)
    private Propriedade propriedade;
}
