package com.gs.agroid.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtuadorResponseDto extends RepresentationModel<AtuadorResponseDto> {
    private Long id;
    private String tipoAtuador;
    private String modelo;
    private String status;
    private String estadoAtual;
    private Long propriedadeId;
}
