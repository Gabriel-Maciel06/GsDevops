package com.gs.agroid.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PropriedadeRequestDto(
    @NotBlank(message = "O nome da propriedade é obrigatório.")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
    String nome,

    @NotBlank(message = "A localização da propriedade é obrigatória.")
    @Size(max = 255, message = "A localização não pode exceder 255 caracteres.")
    String localizacao,

    @NotNull(message = "O tamanho da propriedade é obrigatório.")
    @Min(value = 0, message = "O tamanho deve ser maior ou igual a zero.")
    BigDecimal tamanho,

    @NotNull(message = "O ID do usuário proprietário é obrigatório.")
    Long usuarioId
) {}
