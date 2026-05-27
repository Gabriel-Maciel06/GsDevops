package com.gs.agroid.service;

import com.gs.agroid.dto.SensorRequestDto;
import com.gs.agroid.dto.SensorResponseDto;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.*;
import com.gs.agroid.repository.PropriedadeRepository;
import com.gs.agroid.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final PropriedadeRepository propriedadeRepository;

    @Transactional
    public SensorResponseDto create(SensorRequestDto dto) {
        Propriedade propriedade = propriedadeRepository.findById(dto.propriedadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + dto.propriedadeId()));

        Sensor sensor;
        if ("UMIDADE".equalsIgnoreCase(dto.tipoSensor())) {
            sensor = SensorUmidade.builder()
                    .modelo(dto.modelo())
                    .status(dto.status().toUpperCase())
                    .propriedade(propriedade)
                    .build();
        } else if ("LUMINOSIDADE".equalsIgnoreCase(dto.tipoSensor())) {
            sensor = SensorLuminosidade.builder()
                    .modelo(dto.modelo())
                    .status(dto.status().toUpperCase())
                    .propriedade(propriedade)
                    .build();
        } else {
            throw new IllegalArgumentException("Tipo de sensor inválido: " + dto.tipoSensor());
        }

        sensor = sensorRepository.save(sensor);
        return convertToDto(sensor);
    }

    @Transactional(readOnly = true)
    public Page<SensorResponseDto> findAll(Pageable pageable) {
        return sensorRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<SensorResponseDto> findByPropriedade(Long propriedadeId) {
        return sensorRepository.findByPropriedadeId(propriedadeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SensorResponseDto findById(Long id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado com ID: " + id));
        return convertToDto(sensor);
    }

    @Transactional
    public SensorResponseDto update(Long id, SensorRequestDto dto) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado com ID: " + id));

        Propriedade propriedade = propriedadeRepository.findById(dto.propriedadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada com ID: " + dto.propriedadeId()));

        sensor.setModelo(dto.modelo());
        sensor.setStatus(dto.status().toUpperCase());
        sensor.setPropriedade(propriedade);

        sensor = sensorRepository.save(sensor);
        return convertToDto(sensor);
    }

    @Transactional
    public void delete(Long id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado com ID: " + id));
        sensorRepository.delete(sensor);
    }

    public SensorResponseDto convertToDto(Sensor s) {
        return SensorResponseDto.builder()
                .id(s.getId())
                .tipoSensor(s.getTipoSensor())
                .modelo(s.getModelo())
                .status(s.getStatus())
                .propriedadeId(s.getPropriedade().getId())
                .build();
    }
}
