package com.gs.agroid.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SateliteDadosRequestDto(
    @NotNull(message = "A umidade prevista é obrigatória.")
    @Min(value = 0, message = "A umidade prevista não pode ser negativa.")
    BigDecimal umidadePrevista,

    @NotBlank(message = "O clima é obrigatório.")
    @Size(max = 50, message = "O clima não pode exceder 50 caracteres.")
    String clima,

    @NotBlank(message = "A região é obrigatória.")
    @Size(max = 100, message = "A região não pode exceder 100 caracteres.")
    String regiao,

    LocalDateTime timestamp
) {}
