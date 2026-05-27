package com.gs.agroid.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorResponseDto extends RepresentationModel<SensorResponseDto> {
    private Long id;
    private String tipoSensor;
    private String modelo;
    private String status;
    private Long propriedadeId;
}
