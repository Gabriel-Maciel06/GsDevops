package com.gs.agroid.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "TB_PROPRIEDADE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Propriedade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_propriedade")
    @SequenceGenerator(name = "seq_propriedade", sequenceName = "SEQ_PROPRIEDADE", allocationSize = 1)
    @Column(name = "id_propriedade")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "localizacao", nullable = false, length = 255)
    private String localizacao;

    @Column(name = "tamanho", nullable = false, precision = 10, scale = 2)
    private BigDecimal tamanho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
