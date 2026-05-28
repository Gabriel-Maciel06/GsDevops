package com.gs.agroid.repository;

import com.gs.agroid.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Configura automaticamente o H2 em memória e a camada JPA para teste
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void shouldSaveAndFindByEmail() {
        Usuario usuario = Usuario.builder()
                .nome("Gabriel Integrado")
                .email("gabriel.integrado@test.com")
                .senha("hash_bcrypt_senha")
                .perfil("USER")
                .build();

        usuarioRepository.save(usuario);

        Optional<Usuario> foundUser = usuarioRepository.findByEmail("gabriel.integrado@test.com");

        assertTrue(foundUser.isPresent());
        assertEquals("Gabriel Integrado", foundUser.get().getNome());
        assertEquals("USER", foundUser.get().getPerfil());
    }

    @Test
    void shouldReturnEmptyWhenFindByEmailNonExisting() {
        Optional<Usuario> foundUser = usuarioRepository.findByEmail("nonexisting@test.com");
        assertFalse(foundUser.isPresent());
    }
}
