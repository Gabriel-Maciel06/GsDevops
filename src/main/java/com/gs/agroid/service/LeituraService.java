package com.gs.agroid.service;

import com.gs.agroid.dto.LeituraRequestDto;
import com.gs.agroid.dto.LeituraResponseDto;
import com.gs.agroid.exception.CustomValidationException;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.*;
import com.gs.agroid.repository.AlertaRepository;
import com.gs.agroid.repository.LeituraRepository;
import com.gs.agroid.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeituraService {

    private final LeituraRepository leituraRepository;
    private final SensorRepository sensorRepository;
    private final AlertaRepository alertaRepository;

    @Transactional
    public LeituraResponseDto create(LeituraRequestDto dto) {
        Sensor sensor = sensorRepository.findById(dto.sensorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado com ID: " + dto.sensorId()));

        // Validação dinâmica: umidade deve ser de 0 a 100%
        if ("UMIDADE".equalsIgnoreCase(sensor.getTipoSensor())) {
            if (dto.valor().compareTo(BigDecimal.ZERO) < 0 || dto.valor().compareTo(new BigDecimal("100.0")) > 0) {
                throw new CustomValidationException("Leitura de umidade inválida: " + dto.valor() + "%. Deve estar entre 0% e 100%.");
            }
        }

        LocalDateTime timestamp = dto.timestamp() != null ? dto.timestamp() : LocalDateTime.now();

        LeituraId id = LeituraId.builder()
                .sensorId(sensor.getId())
                .timestamp(timestamp)
                .build();

        Leitura leitura = Leitura.builder()
                .id(id)
                .sensor(sensor)
                .valor(dto.valor())
                .build();

        leitura = leituraRepository.save(leitura);

        // Lógica de Integração Estratégica: Irrigação Automática
        // Se a leitura for de umidade e estiver abaixo de 20%, dispara o sistema de irrigação e gera alerta
        if ("UMIDADE".equalsIgnoreCase(sensor.getTipoSensor()) && dto.valor().compareTo(new BigDecimal("20.00")) < 0) {
            triggerIrrigacao(sensor.getPropriedade(), dto.valor());
        }

        return convertToDto(leitura);
    }

    private void triggerIrrigacao(Propriedade propriedade, BigDecimal valorUmidade) {
        System.out.println("[SISTEMA DE IRRIGAÇÃO] >>> DISPARANDO IRRIGAÇÃO AUTOMÁTICA para a propriedade: " 
                + propriedade.getNome() + " (Umidade atual: " + valorUmidade + "%)");

        Alerta alerta = Alerta.builder()
                .mensagem("SISTEMA DE IRRIGAÇÃO ATIVADO: Umidade crítica detectada de " + valorUmidade + "% na propriedade: " + propriedade.getNome())
                .timestamp(LocalDateTime.now())
                .propriedade(propriedade)
                .build();

        alertaRepository.save(alerta);
    }

    @Transactional(readOnly = true)
    public Page<LeituraResponseDto> findBySensor(Long sensorId, Pageable pageable) {
        return leituraRepository.findByIdSensorIdOrderByIdTimestampDesc(sensorId, pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<LeituraResponseDto> findBySensor(Long sensorId) {
        return leituraRepository.findByIdSensorIdOrderByIdTimestampDesc(sensorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LeituraResponseDto findById(Long sensorId, LocalDateTime timestamp) {
        LeituraId id = new LeituraId(sensorId, timestamp);
        Leitura leitura = leituraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada para o sensor " + sensorId + " no timestamp " + timestamp));
        return convertToDto(leitura);
    }

    @Transactional
    public LeituraResponseDto update(Long sensorId, LocalDateTime timestamp, LeituraRequestDto dto) {
        LeituraId id = new LeituraId(sensorId, timestamp);
        Leitura leitura = leituraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada para o sensor " + sensorId + " no timestamp " + timestamp));

        Sensor sensor = sensorRepository.findById(dto.sensorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado com ID: " + dto.sensorId()));

        if ("UMIDADE".equalsIgnoreCase(sensor.getTipoSensor())) {
            if (dto.valor().compareTo(BigDecimal.ZERO) < 0 || dto.valor().compareTo(new BigDecimal("100.0")) > 0) {
                throw new CustomValidationException("Leitura de umidade inválida: " + dto.valor() + "%. Deve estar entre 0% e 100%.");
            }
        }

        leitura.setValor(dto.valor());
        leitura = leituraRepository.save(leitura);
        return convertToDto(leitura);
    }

    @Transactional
    public void delete(Long sensorId, LocalDateTime timestamp) {
        LeituraId id = new LeituraId(sensorId, timestamp);
        Leitura leitura = leituraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada para o sensor " + sensorId + " no timestamp " + timestamp));
        leituraRepository.delete(leitura);
    }

    public LeituraResponseDto convertToDto(Leitura l) {
        return LeituraResponseDto.builder()
                .sensorId(l.getId().getSensorId())
                .timestamp(l.getId().getTimestamp())
                .valor(l.getValor())
                .build();
    }
}
