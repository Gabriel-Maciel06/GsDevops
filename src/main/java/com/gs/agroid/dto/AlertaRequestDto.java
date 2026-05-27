package com.gs.agroid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertaRequestDto(
    @NotBlank(message = "A mensagem do alerta é obrigatória.")
    @Size(max = 255, message = "A mensagem não pode exceder 255 caracteres.")
    String mensagem,

    @NotNull(message = "O ID da propriedade é obrigatório.")
    Long propriedadeId
) {}
