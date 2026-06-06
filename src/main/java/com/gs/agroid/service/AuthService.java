package com.gs.agroid.service;

import com.gs.agroid.dto.LoginRequestDto;
import com.gs.agroid.dto.LoginResponseDto;
import com.gs.agroid.dto.RegisterRequestDto;
import com.gs.agroid.model.Usuario;
import com.gs.agroid.repository.UsuarioRepository;
import com.gs.agroid.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + username));
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos."));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("Usuário ou senha inválidos.");
        }

        String token = tokenService.generateToken(usuario);
        return new LoginResponseDto(token, usuario.getEmail(), usuario.getPerfil());
    }

    @Transactional
    public LoginResponseDto register(RegisterRequestDto dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        String perfil = dto.perfil() != null ? dto.perfil().toUpperCase() : "USER";
        if ("ADMIN".equalsIgnoreCase(perfil) || "ESP32".equalsIgnoreCase(perfil)) {
            throw new IllegalArgumentException("Registro de administrador ou dispositivo IoT não é permitido publicamente.");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .perfil(perfil)
                .build();

        usuario = usuarioRepository.save(usuario);
        String token = tokenService.generateToken(usuario);
        return new LoginResponseDto(token, usuario.getEmail(), usuario.getPerfil());
    }
}
