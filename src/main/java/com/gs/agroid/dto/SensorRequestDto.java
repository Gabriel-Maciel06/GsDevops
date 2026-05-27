package com.gs.agroid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SensorRequestDto(
    @NotBlank(message = "O tipo do sensor é obrigatório.")
    @Pattern(regexp = "UMIDADE|LUMINOSIDADE", message = "O tipo do sensor deve ser UMIDADE ou LUMINOSIDADE.")
    String tipoSensor,

    @NotBlank(message = "O modelo do sensor é obrigatório.")
    String modelo,

    @NotBlank(message = "O status do sensor é obrigatório.")
    @Pattern(regexp = "ATIVO|INATIVO", message = "O status deve ser ATIVO ou INATIVO.")
    String status,

    @NotNull(message = "O ID da propriedade vinculada é obrigatório.")
    Long propriedadeId
) {}
