package com.gs.agroid.service;

import com.gs.agroid.dto.AlertaRequestDto;
import com.gs.agroid.dto.AlertaResponseDto;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.Alerta;
import com.gs.agroid.model.Propriedade;
import com.gs.agroid.repository.AlertaRepository;
import com.gs.agroid.repository.PropriedadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final PropriedadeRepository propriedadeRepository;

    @Transactional
    public AlertaResponseDto create(AlertaRequestDto dto) {
        Propriedade propriedade = propriedadeRepository.findById(dto.propriedadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + dto.propriedadeId()));

        Alerta alerta = Alerta.builder()
                .mensagem(dto.mensagem())
                .timestamp(LocalDateTime.now())
                .propriedade(propriedade)
                .build();

        alerta = alertaRepository.save(alerta);
        return convertToDto(alerta);
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponseDto> findAll(Pageable pageable) {
        return alertaRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponseDto> findByPropriedade(Long propriedadeId, Pageable pageable) {
        return alertaRepository.findByPropriedadeIdOrderByTimestampDesc(propriedadeId, pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<AlertaResponseDto> findByPropriedade(Long propriedadeId) {
        return alertaRepository.findByPropriedadeIdOrderByTimestampDesc(propriedadeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlertaResponseDto findById(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado com ID: " + id));
        return convertToDto(alerta);
    }

    @Transactional
    public AlertaResponseDto update(Long id, AlertaRequestDto dto) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado com ID: " + id));

        Propriedade propriedade = propriedadeRepository.findById(dto.propriedadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + dto.propriedadeId()));

        alerta.setMensagem(dto.mensagem());
        alerta.setPropriedade(propriedade);
        alerta = alertaRepository.save(alerta);

        return convertToDto(alerta);
    }

    @Transactional
    public void delete(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado com ID: " + id));
        alertaRepository.delete(alerta);
    }

    public AlertaResponseDto convertToDto(Alerta a) {
        return AlertaResponseDto.builder()
                .id(a.getId())
                .mensagem(a.getMensagem())
                .timestamp(a.getTimestamp())
                .propriedadeId(a.getPropriedade().getId())
                .build();
    }
}
