package com.gs.agroid.controller;

import com.gs.agroid.dto.LeituraRequestDto;
import com.gs.agroid.dto.LeituraResponseDto;
import com.gs.agroid.service.LeituraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/leituras")
@RequiredArgsConstructor
public class LeituraController {

    private final LeituraService leituraService;

    @PostMapping
    public ResponseEntity<LeituraResponseDto> create(@RequestBody @Valid LeituraRequestDto dto) {
        LeituraResponseDto response = leituraService.create(dto);
        addHateoasLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<LeituraResponseDto> findById(
            @RequestParam Long sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp) {
        LeituraResponseDto response = leituraService.findById(sensorId, timestamp);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<LeituraResponseDto> update(
            @RequestParam Long sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,
            @RequestBody @Valid LeituraRequestDto dto) {
        LeituraResponseDto response = leituraService.update(sensorId, timestamp, dto);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam Long sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp) {
        leituraService.delete(sensorId, timestamp);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinks(LeituraResponseDto dto) {
        dto.add(Link.of("/api/leituras?sensorId=" + dto.getSensorId() + "&timestamp=" + dto.getTimestamp().toString()).withSelfRel());
        dto.add(linkTo(methodOn(SensorController.class).findById(dto.getSensorId())).withRel("sensor"));
    }
}
