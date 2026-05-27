package com.gs.agroid.dto;

public record LoginResponseDto(
    String token,
    String email,
    String perfil
) {}
