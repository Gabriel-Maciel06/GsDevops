package com.gs.agroid.service;

import com.gs.agroid.dto.SateliteDadosRequestDto;
import com.gs.agroid.dto.SateliteDadosResponseDto;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.SateliteDados;
import com.gs.agroid.repository.SateliteDadosRepository;
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
public class SateliteDadosService {

    private final SateliteDadosRepository sateliteDadosRepository;

    @Transactional
    public SateliteDadosResponseDto create(SateliteDadosRequestDto dto) {
        LocalDateTime timestamp = dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now();

        SateliteDados dados = SateliteDados.builder()
                .umidadePrevista(dto.umidadePrevista())
                .clima(dto.clima())
                .regiao(dto.regiao())
                .timestamp(timestamp)
                .build();

        dados = sateliteDadosRepository.save(dados);
        return convertToDto(dados);
    }

    @Transactional(readOnly = true)
    public Page<SateliteDadosResponseDto> findAll(Pageable pageable) {
        return sateliteDadosRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<SateliteDadosResponseDto> findByRegiao(String regiao, Pageable pageable) {
        return sateliteDadosRepository.findByRegiaoOrderByTimestampDesc(regiao, pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<SateliteDadosResponseDto> findByRegiao(String regiao) {
        return sateliteDadosRepository.findByRegiaoOrderByTimestampDesc(regiao).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SateliteDadosResponseDto findById(Long id) {
        SateliteDados dados = sateliteDadosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dados de satélite não encontrados com ID: " + id));
        return convertToDto(dados);
    }

    @Transactional
    public SateliteDadosResponseDto update(Long id, SateliteDadosRequestDto dto) {
        SateliteDados dados = sateliteDadosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dados de satélite não encontrados com ID: " + id));

        LocalDateTime timestamp = dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now();

        dados.setUmidadePrevista(dto.umidadePrevista());
        dados.setClima(dto.clima());
        dados.setRegiao(dto.regiao());
        dados.setTimestamp(timestamp);

        dados = sateliteDadosRepository.save(dados);
        return convertToDto(dados);
    }

    @Transactional
    public void delete(Long id) {
        SateliteDados dados = sateliteDadosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dados de satélite não encontrados com ID: " + id));
        sateliteDadosRepository.delete(dados);
    }

    public SateliteDadosResponseDto convertToDto(SateliteDados s) {
        return SateliteDadosResponseDto.builder()
                .id(s.getId())
                .umidadePrevista(s.getUmidadePrevista())
                .clima(s.getClima())
                .regiao(s.getRegiao())
                .timestamp(s.getTimestamp())
                .build();
    }
}
