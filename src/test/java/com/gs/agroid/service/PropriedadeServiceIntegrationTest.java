package com.gs.agroid.service;

import com.gs.agroid.dto.PropriedadeRequestDto;
import com.gs.agroid.dto.PropriedadeResponseDto;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.Propriedade;
import com.gs.agroid.model.Usuario;
import com.gs.agroid.repository.PropriedadeRepository;
import com.gs.agroid.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Garante rollback do banco após cada caso de teste
class PropriedadeServiceIntegrationTest {

    @Autowired
    private PropriedadeService propriedadeService;

    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        // Limpa coleções para garantir independência entre execuções
        propriedadeRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Cria o usuário necessário para vincular as propriedades
        usuarioTeste = Usuario.builder()
                .nome("Produtor Teste")
                .email("produtor.teste@aerosoil.com")
                .senha("$2a$10$3z.T7B3X9k4.1234567890abcdef1234567890abcdef1234") // senha fictícia BCrypt
                .perfil("USER")
                .build();
        usuarioTeste = usuarioRepository.save(usuarioTeste);
    }

    @Test
    void shouldCreatePropriedadeSuccessfully() {
        PropriedadeRequestDto request = new PropriedadeRequestDto(
                "Fazenda Sol Nascente",
                "Ribeirão Preto - SP",
                BigDecimal.valueOf(250.75),
                usuarioTeste.getId()
        );

        PropriedadeResponseDto response = propriedadeService.create(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Fazenda Sol Nascente", response.getNome());
        assertEquals("Ribeirão Preto - SP", response.getLocalizacao());
        assertEquals(0, BigDecimal.valueOf(250.75).compareTo(response.getTamanho()));
        assertEquals(usuarioTeste.getId(), response.getUsuarioId());

        // Valida persistência real no banco de dados H2
        Optional<Propriedade> persistido = propriedadeRepository.findById(response.getId());
        assertTrue(persistido.isPresent());
        assertEquals("Fazenda Sol Nascente", persistido.get().getNome());
    }

    @Test
    void shouldFailCreatingPropriedadeWhenUsuarioNotFound() {
        PropriedadeRequestDto request = new PropriedadeRequestDto(
                "Fazenda Sem Dono",
                "Desconhecido",
                BigDecimal.valueOf(50.0),
                9999L // ID de usuário inexistente
        );

        assertThrows(ResourceNotFoundException.class, () -> {
            propriedadeService.create(request);
        });
    }

    @Test
    void shouldFindPropriedadeById() {
        Propriedade propriedade = Propriedade.builder()
                .nome("Fazenda Recanto")
                .localizacao("Uberlândia - MG")
                .tamanho(BigDecimal.valueOf(180.00))
                .usuario(usuarioTeste)
                .build();
        propriedade = propriedadeRepository.save(propriedade);

        PropriedadeResponseDto response = propriedadeService.findById(propriedade.getId());

        assertNotNull(response);
        assertEquals(propriedade.getId(), response.getId());
        assertEquals("Fazenda Recanto", response.getNome());
    }

    @Test
    void shouldFailFindingPropriedadeWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            propriedadeService.findById(9999L);
        });
    }

    @Test
    void shouldFindAllPropriedadesWithPagination() {
        Propriedade p1 = Propriedade.builder()
                .nome("Fazenda A")
                .localizacao("Local A")
                .tamanho(BigDecimal.valueOf(100))
                .usuario(usuarioTeste)
                .build();
        Propriedade p2 = Propriedade.builder()
                .nome("Fazenda B")
                .localizacao("Local B")
                .tamanho(BigDecimal.valueOf(200))
                .usuario(usuarioTeste)
                .build();
        propriedadeRepository.saveAll(List.of(p1, p2));

        Page<PropriedadeResponseDto> page = propriedadeService.findAll(PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void shouldFindPropriedadesByUsuario() {
        Propriedade p1 = Propriedade.builder()
                .nome("Fazenda X")
                .localizacao("Local X")
                .tamanho(BigDecimal.valueOf(150))
                .usuario(usuarioTeste)
                .build();
        propriedadeRepository.save(p1);

        List<PropriedadeResponseDto> list = propriedadeService.findByUsuario(usuarioTeste.getId());

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Fazenda X", list.get(0).getNome());
    }

    @Test
    void shouldUpdatePropriedadeSuccessfully() {
        Propriedade propriedade = Propriedade.builder()
                .nome("Sítio das Flores")
                .localizacao("Atibaia - SP")
                .tamanho(BigDecimal.valueOf(45.50))
                .usuario(usuarioTeste)
                .build();
        propriedade = propriedadeRepository.save(propriedade);

        PropriedadeRequestDto updateRequest = new PropriedadeRequestDto(
                "Sítio das Flores Alterado",
                "Atibaia - SP Novo",
                BigDecimal.valueOf(50.00),
                usuarioTeste.getId()
        );

        PropriedadeResponseDto response = propriedadeService.update(propriedade.getId(), updateRequest);

        assertNotNull(response);
        assertEquals("Sítio das Flores Alterado", response.getNome());
        assertEquals("Atibaia - SP Novo", response.getLocalizacao());
        assertEquals(0, BigDecimal.valueOf(50.00).compareTo(response.getTamanho()));

        // Valida se foi persistido corretamente no banco de dados H2
        Propriedade atualizado = propriedadeRepository.findById(propriedade.getId()).orElseThrow();
        assertEquals("Sítio das Flores Alterado", atualizado.getNome());
        assertEquals("Atibaia - SP Novo", atualizado.getLocalizacao());
    }

    @Test
    void shouldDeletePropriedadeSuccessfully() {
        Propriedade propriedade = Propriedade.builder()
                .nome("Chácara Bella Vista")
                .localizacao("Campinas - SP")
                .tamanho(BigDecimal.valueOf(12.30))
                .usuario(usuarioTeste)
                .build();
        propriedade = propriedadeRepository.save(propriedade);

        // Garante que existe no banco
        assertTrue(propriedadeRepository.existsById(propriedade.getId()));

        // Deleta via serviço
        propriedadeService.delete(propriedade.getId());

        // Garante que foi removido do H2
        assertFalse(propriedadeRepository.existsById(propriedade.getId()));
    }
}
