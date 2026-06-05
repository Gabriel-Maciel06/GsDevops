package com.gs.agroid.controller;

import com.gs.agroid.dto.LeituraResponseDto;
import com.gs.agroid.dto.SensorRequestDto;
import com.gs.agroid.dto.SensorResponseDto;
import com.gs.agroid.service.LeituraService;
import com.gs.agroid.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/sensores")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;
    private final LeituraService leituraService;

    @PostMapping
    public ResponseEntity<SensorResponseDto> create(@RequestBody @Valid SensorRequestDto dto) {
        SensorResponseDto response = sensorService.create(dto);
        addHateoasLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<SensorResponseDto>>> findAll(
            Pageable pageable,
            PagedResourcesAssembler<SensorResponseDto> assembler) {
        Page<SensorResponseDto> sensores = sensorService.findAll(pageable);
        sensores.forEach(this::addHateoasLinks);
        PagedModel<EntityModel<SensorResponseDto>> pagedModel = assembler.toModel(sensores);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorResponseDto> findById(@PathVariable Long id) {
        SensorResponseDto response = sensorService.findById(id);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    // Endpoint para buscar o histórico de leituras de um sensor específico (paginado)
    @GetMapping("/{id}/leituras")
    public ResponseEntity<PagedModel<EntityModel<LeituraResponseDto>>> getLeituras(
            @PathVariable Long id,
            Pageable pageable,
            PagedResourcesAssembler<LeituraResponseDto> assembler) {
        Page<LeituraResponseDto> leituras = leituraService.findBySensor(id, pageable);
        leituras.forEach(this::addLeituraHateoasLinks);
        PagedModel<EntityModel<LeituraResponseDto>> pagedModel = assembler.toModel(leituras);
        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorResponseDto> update(@PathVariable Long id, @RequestBody @Valid SensorRequestDto dto) {
        SensorResponseDto response = sensorService.update(id, dto);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sensorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinks(SensorResponseDto dto) {
        dto.add(linkTo(methodOn(SensorController.class).findById(dto.getId())).withSelfRel());
        dto.add(Link.of("/api/sensores/" + dto.getId() + "/leituras").withRel("leituras"));
        dto.add(linkTo(methodOn(PropriedadeController.class).findById(dto.getPropriedadeId())).withRel("propriedade"));
    }

    private void addLeituraHateoasLinks(LeituraResponseDto dto) {
        dto.add(Link.of("/api/leituras?sensorId=" + dto.getSensorId() + "&timestamp=" + dto.getTimestamp().toString()).withSelfRel());
        dto.add(linkTo(methodOn(SensorController.class).findById(dto.getSensorId())).withRel("sensor"));
    }
}
