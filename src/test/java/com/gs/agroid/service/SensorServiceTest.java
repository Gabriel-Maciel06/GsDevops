package com.gs.agroid.service;

import com.gs.agroid.dto.SensorRequestDto;
import com.gs.agroid.dto.SensorResponseDto;
import com.gs.agroid.exception.ResourceNotFoundException;
import com.gs.agroid.model.Propriedade;
import com.gs.agroid.model.Sensor;
import com.gs.agroid.model.SensorUmidade;
import com.gs.agroid.repository.PropriedadeRepository;
import com.gs.agroid.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SensorServiceTest {

    @InjectMocks
    private SensorService sensorService;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private PropriedadeRepository propriedadeRepository;

    private Propriedade propriedade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        propriedade = Propriedade.builder()
                .id(10L)
                .nome("Fazenda Verde")
                .localizacao("Interior de SP")
                .tamanho(java.math.BigDecimal.valueOf(100.5))
                .build();
    }

    @Test
    void shouldCreateSensorSuccessfully() {
        SensorRequestDto dto = new SensorRequestDto("UMIDADE", "DHT22", "ATIVO", 10L);

        when(propriedadeRepository.findById(10L)).thenReturn(Optional.of(propriedade));
        
        SensorUmidade sensorSalvo = SensorUmidade.builder()
                .id(1L)
                .modelo(dto.modelo())
                .status("ATIVO")
                .propriedade(propriedade)
                .build();
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensorSalvo);

        SensorResponseDto response = sensorService.create(dto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("UMIDADE", response.getTipoSensor());
        assertEquals("DHT22", response.getModelo());
        assertEquals("ATIVO", response.getStatus());
        assertEquals(10L, response.getPropriedadeId());

        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void shouldFailCreatingSensorWhenPropriedadeNotFound() {
        SensorRequestDto dto = new SensorRequestDto("UMIDADE", "DHT22", "ATIVO", 99L);
        when(propriedadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            sensorService.create(dto);
        });

        verify(sensorRepository, never()).save(any(Sensor.class));
    }

    @Test
    void shouldFindSensorById() {
        SensorUmidade sensor = SensorUmidade.builder()
                .id(1L)
                .modelo("DHT22")
                .status("ATIVO")
                .propriedade(propriedade)
                .build();

        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));

        SensorResponseDto response = sensorService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("DHT22", response.getModelo());
    }

    @Test
    void shouldFailFindingSensorByIdWhenNotFound() {
        when(sensorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            sensorService.findById(99L);
        });
    }

    @Test
    void shouldUpdateSensorSuccessfully() {
        SensorRequestDto dto = new SensorRequestDto("UMIDADE", "DHT22 Updated", "INATIVO", 10L);
        SensorUmidade sensorExistente = SensorUmidade.builder()
                .id(1L)
                .modelo("DHT22")
                .status("ATIVO")
                .propriedade(propriedade)
                .build();

        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensorExistente));
        when(propriedadeRepository.findById(10L)).thenReturn(Optional.of(propriedade));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensorExistente);

        SensorResponseDto response = sensorService.update(1L, dto);

        assertNotNull(response);
        assertEquals("DHT22 Updated", response.getModelo());
        assertEquals("INATIVO", response.getStatus());
    }

    @Test
    void shouldDeleteSensorSuccessfully() {
        SensorUmidade sensor = SensorUmidade.builder()
                .id(1L)
                .modelo("DHT22")
                .status("ATIVO")
                .propriedade(propriedade)
                .build();

        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));
        doNothing().when(sensorRepository).delete(sensor);

        assertDoesNotThrow(() -> {
            sensorService.delete(1L);
        });

        verify(sensorRepository, times(1)).delete(sensor);
    }
}
