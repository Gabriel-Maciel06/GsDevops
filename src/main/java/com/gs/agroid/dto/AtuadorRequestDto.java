package com.gs.agroid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtuadorRequestDto(
    @NotBlank(message = "O tipo do atuador é obrigatório.")
    @Pattern(regexp = "BOMBA_AGUA|VALVULA_SOLENOIDE|ASPERSOR", message = "O tipo do atuador deve ser BOMBA_AGUA, VALVULA_SOLENOIDE ou ASPERSOR.")
    String tipoAtuador,

    @NotBlank(message = "O modelo do atuador é obrigatório.")
    @Size(max = 100, message = "O modelo não pode exceder 100 caracteres.")
    String modelo,

    @NotBlank(message = "O status do atuador é obrigatório.")
    @Pattern(regexp = "ATIVO|INATIVO", message = "O status deve ser ATIVO ou INATIVO.")
    String status,

    @NotBlank(message = "O estado atual do atuador é obrigatório.")
    @Pattern(regexp = "LIGADO|DESLIGADO", message = "O estado atual deve ser LIGADO ou DESLIGADO.")
    String estadoAtual,

    @NotNull(message = "O ID da propriedade é obrigatório.")
    Long propriedadeId
) {}
