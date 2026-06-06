package com.gs.agroid.service;

import com.gs.agroid.dto.AtuadorRequestDto;
import com.gs.agroid.dto.AtuadorResponseDto;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.Atuador;
import com.gs.agroid.model.Propriedade;
import com.gs.agroid.repository.AtuadorRepository;
import com.gs.agroid.repository.PropriedadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AtuadorService {

    private final AtuadorRepository atuadorRepository;
    private final PropriedadeRepository propriedadeRepository;

    @Transactional
    public AtuadorResponseDto create(AtuadorRequestDto dto) {
        Propriedade propriedade = propriedadeRepository.findById(dto.propriedadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + dto.propriedadeId()));

        Atuador atuador = Atuador.builder()
                .tipoAtuador(dto.tipoAtuador().toUpperCase())
                .modelo(dto.modelo())
                .status(dto.status().toUpperCase())
                .estadoAtual(dto.estadoAtual().toUpperCase())
                .propriedade(propriedade)
                .build();

        atuador = atuadorRepository.save(atuador);
        return convertToDto(atuador);
    }

    @Transactional(readOnly = true)
    public Page<AtuadorResponseDto> findAll(Pageable pageable) {
        return atuadorRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<AtuadorResponseDto> findByPropriedade(Long propriedadeId, Pageable pageable) {
        return atuadorRepository.findByPropriedadeId(propriedadeId, pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<AtuadorResponseDto> findByPropriedade(Long propriedadeId) {
        return atuadorRepository.findByPropriedadeId(propriedadeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtuadorResponseDto findById(Long id) {
        Atuador atuador = atuadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atuador não encontrado com ID: " + id));
        return convertToDto(atuador);
    }

    @Transactional
    public AtuadorResponseDto update(Long id, AtuadorRequestDto dto) {
        Atuador atuador = atuadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atuador não encontrado com ID: " + id));

        Propriedade propriedade = propriedadeRepository.findById(dto.propriedadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + dto.propriedadeId()));

        atuador.setTipoAtuador(dto.tipoAtuador().toUpperCase());
        atuador.setModelo(dto.modelo());
        atuador.setStatus(dto.status().toUpperCase());
        atuador.setEstadoAtual(dto.estadoAtual().toUpperCase());
        atuador.setPropriedade(propriedade);

        atuador = atuadorRepository.save(atuador);
        return convertToDto(atuador);
    }

    @Transactional
    public AtuadorResponseDto toggleEstado(Long id) {
        Atuador atuador = atuadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atuador não encontrado com ID: " + id));

        String novoEstado = "LIGADO".equalsIgnoreCase(atuador.getEstadoAtual()) ? "DESLIGADO" : "LIGADO";
        atuador.setEstadoAtual(novoEstado);

        atuador = atuadorRepository.save(atuador);
        return convertToDto(atuador);
    }

    @Transactional
    public void delete(Long id) {
        Atuador atuador = atuadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atuador não encontrado com ID: " + id));
        atuadorRepository.delete(atuador);
    }

    public AtuadorResponseDto convertToDto(Atuador a) {
        return AtuadorResponseDto.builder()
                .id(a.getId())
                .tipoAtuador(a.getTipoAtuador())
                .modelo(a.getModelo())
                .status(a.getStatus())
                .estadoAtual(a.getEstadoAtual())
                .propriedadeId(a.getPropriedade().getId())
                .build();
    }
}
