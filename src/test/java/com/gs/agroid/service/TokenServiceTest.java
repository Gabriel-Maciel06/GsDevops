package com.gs.agroid.service;

import com.gs.agroid.model.Usuario;
import com.gs.agroid.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-key-gs-agro-id-2026");
    }

    @Test
    void shouldGenerateAndValidateToken() {
        Usuario usuario = Usuario.builder()
                .nome("Test User")
                .email("test@email.com")
                .perfil("USER")
                .build();

        String token = tokenService.generateToken(usuario);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String email = tokenService.validateToken(token);
        assertEquals("test@email.com", email);
    }
}
