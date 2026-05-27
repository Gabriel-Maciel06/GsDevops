package com.gs.agroid.service;

import com.gs.agroid.dto.PropriedadeRequestDto;
import com.gs.agroid.dto.PropriedadeResponseDto;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.Propriedade;
import com.gs.agroid.model.Usuario;
import com.gs.agroid.repository.PropriedadeRepository;
import com.gs.agroid.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropriedadeService {

    private final PropriedadeRepository propriedadeRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PropriedadeResponseDto create(PropriedadeRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário proprietário não encontrado com ID: " + dto.usuarioId()));

        Propriedade propriedade = Propriedade.builder()
                .nome(dto.nome())
                .localizacao(dto.localizacao())
                .tamanho(dto.tamanho())
                .usuario(usuario)
                .build();

        propriedade = propriedadeRepository.save(propriedade);
        return convertToDto(propriedade);
    }

    @Transactional(readOnly = true)
    public Page<PropriedadeResponseDto> findAll(Pageable pageable) {
        return propriedadeRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public PropriedadeResponseDto findById(Long id) {
        Propriedade propriedade = propriedadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + id));
        return convertToDto(propriedade);
    }

    @Transactional(readOnly = true)
    public List<PropriedadeResponseDto> findByUsuario(Long usuarioId) {
        return propriedadeRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PropriedadeResponseDto update(Long id, PropriedadeRequestDto dto) {
        Propriedade propriedade = propriedadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + id));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário proprietário não encontrado com ID: " + dto.usuarioId()));

        propriedade.setNome(dto.nome());
        propriedade.setLocalizacao(dto.localizacao());
        propriedade.setTamanho(dto.tamanho());
        propriedade.setUsuario(usuario);

        propriedade = propriedadeRepository.save(propriedade);
        return convertToDto(propriedade);
    }

    @Transactional
    public void delete(Long id) {
        Propriedade propriedade = propriedadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + id));
        propriedadeRepository.delete(propriedade);
    }

    public PropriedadeResponseDto convertToDto(Propriedade p) {
        return PropriedadeResponseDto.builder()
                .id(p.getId())
                .nome(p.getNome())
                .localizacao(p.getLocalizacao())
                .tamanho(p.getTamanho())
                .usuarioId(p.getUsuario().getId())
                .build();
    }
}
