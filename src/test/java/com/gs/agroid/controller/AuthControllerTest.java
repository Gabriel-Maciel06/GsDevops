package com.gs.agroid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.agroid.dto.LoginRequestDto;
import com.gs.agroid.dto.LoginResponseDto;
import com.gs.agroid.dto.RegisterRequestDto;
import com.gs.agroid.security.JwtTokenFilter;
import com.gs.agroid.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desabilita os filtros de segurança nos testes de controller para simplificar o mock
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnOkOnLogin() throws Exception {
        LoginRequestDto request = new LoginRequestDto("user@test.com", "password123");
        LoginResponseDto response = new LoginResponseDto("mocked_token", "user@test.com", "USER");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked_token"))
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.perfil").value("USER"));
    }

    @Test
    void shouldReturnCreatedOnRegister() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("New User", "new@test.com", "password123", "USER");
        LoginResponseDto response = new LoginResponseDto("mocked_token", "new@test.com", "USER");

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mocked_token"))
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.perfil").value("USER"));
    }

    @Test
    void shouldReturnBadRequestOnLoginWithInvalidEmail() throws Exception {
        LoginRequestDto request = new LoginRequestDto("", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestOnRegisterWithShortPassword() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("New User", "new@test.com", "123", "USER");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
