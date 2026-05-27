package com.gs.agroid.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraRequestDto(
    @NotNull(message = "O ID do sensor é obrigatório.")
    Long sensorId,

    @NotNull(message = "O valor da leitura é obrigatório.")
    @Min(value = 0, message = "O valor da leitura não pode ser negativo.")
    BigDecimal valor,

    LocalDateTime timestamp
) {}
