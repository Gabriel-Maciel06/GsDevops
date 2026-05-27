package com.gs.agroid.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropriedadeResponseDto extends RepresentationModel<PropriedadeResponseDto> {
    private Long id;
    private String nome;
    private String localizacao;
    private BigDecimal tamanho;
    private Long usuarioId;
}
