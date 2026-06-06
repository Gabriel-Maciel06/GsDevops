package com.gs.agroid.service;

import com.gs.agroid.dto.LoginRequestDto;
import com.gs.agroid.dto.LoginResponseDto;
import com.gs.agroid.dto.RegisterRequestDto;
import com.gs.agroid.model.Usuario;
import com.gs.agroid.repository.UsuarioRepository;
import com.gs.agroid.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDto dto = new RegisterRequestDto("Gabriel", "gabriel@test.com", "senha123", "USER");

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.senha())).thenReturn("encoded_senha");

        Usuario savedUser = Usuario.builder()
                .id(1L)
                .nome(dto.nome())
                .email(dto.email())
                .senha("encoded_senha")
                .perfil("USER")
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);
        when(tokenService.generateToken(any(Usuario.class))).thenReturn("jwt_token");

        LoginResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("gabriel@test.com", response.email());
        assertEquals("USER", response.perfil());
        assertEquals("jwt_token", response.token());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void shouldFailRegisteringAdminPublicly() {
        RegisterRequestDto dto = new RegisterRequestDto("Admin User", "admin@test.com", "senha123", "ADMIN");

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(dto);
        });

        assertEquals("Registro de administrador ou dispositivo IoT não é permitido publicamente.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldFailRegisteringEsp32Publicly() {
        RegisterRequestDto dto = new RegisterRequestDto("IoT Device", "esp32@test.com", "senha123", "ESP32");

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(dto);
        });

        assertEquals("Registro de administrador ou dispositivo IoT não é permitido publicamente.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequestDto dto = new LoginRequestDto("gabriel@test.com", "senha123");
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Gabriel")
                .email("gabriel@test.com")
                .senha("encoded_senha")
                .perfil("USER")
                .build();

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(dto.senha(), usuario.getSenha())).thenReturn(true);
        when(tokenService.generateToken(usuario)).thenReturn("jwt_token");

        LoginResponseDto response = authService.login(dto);

        assertNotNull(response);
        assertEquals("jwt_token", response.token());
        assertEquals("USER", response.perfil());
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        LoginRequestDto dto = new LoginRequestDto("gabriel@test.com", "wrong_senha");
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Gabriel")
                .email("gabriel@test.com")
                .senha("encoded_senha")
                .perfil("USER")
                .build();

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(dto.senha(), usuario.getSenha())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(dto);
        });
    }

    @Test
    void shouldFailRegisteringDuplicateEmail() {
        RegisterRequestDto dto = new RegisterRequestDto("Gabriel", "gabriel@test.com", "senha123", "USER");
        Usuario existingUser = Usuario.builder()
                .id(1L)
                .email("gabriel@test.com")
                .build();

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(dto);
        });
    }

    @Test
    void shouldFailLoginWithNonExistingUser() {
        LoginRequestDto dto = new LoginRequestDto("nonexistent@test.com", "senha123");

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(dto);
        });
    }
}
