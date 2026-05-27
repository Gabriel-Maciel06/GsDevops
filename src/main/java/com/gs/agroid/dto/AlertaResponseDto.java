package com.gs.agroid.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaResponseDto extends RepresentationModel<AlertaResponseDto> {
    private Long id;
    private String mensagem;
    private LocalDateTime timestamp;
    private Long propriedadeId;
}
