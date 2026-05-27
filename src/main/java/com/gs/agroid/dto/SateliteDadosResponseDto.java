package com.gs.agroid.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SateliteDadosResponseDto extends RepresentationModel<SateliteDadosResponseDto> {
    private Long id;
    private BigDecimal umidadePrevista;
    private String clima;
    private String regiao;
    private LocalDateTime timestamp;
}
