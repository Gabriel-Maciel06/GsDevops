package com.gs.agroid.controller;

import com.gs.agroid.dto.PropriedadeRequestDto;
import com.gs.agroid.dto.PropriedadeResponseDto;
import com.gs.agroid.dto.SensorResponseDto;
import com.gs.agroid.service.PropriedadeService;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class PropriedadeController {

    private final PropriedadeService propriedadeService;
    private final SensorService sensorService;

    @PostMapping
    public ResponseEntity<PropriedadeResponseDto> create(@RequestBody @Valid PropriedadeRequestDto dto) {
        PropriedadeResponseDto response = propriedadeService.create(dto);
        addHateoasLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PropriedadeResponseDto>>> findAll(
            Pageable pageable,
            PagedResourcesAssembler<PropriedadeResponseDto> assembler) {
        Page<PropriedadeResponseDto> propriedades = propriedadeService.findAll(pageable);
        propriedades.forEach(this::addHateoasLinks);
        PagedModel<EntityModel<PropriedadeResponseDto>> pagedModel = assembler.toModel(propriedades);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropriedadeResponseDto> findById(@PathVariable Long id) {
        PropriedadeResponseDto response = propriedadeService.findById(id);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    // Endpoint para retornar os sensores daquela área
    @GetMapping("/{id}/sensores")
    public ResponseEntity<List<SensorResponseDto>> getSensoresByArea(@PathVariable Long id) {
        List<SensorResponseDto> sensores = sensorService.findByPropriedade(id);
        sensores.forEach(this::addSensorHateoasLinks);
        return ResponseEntity.ok(sensores);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropriedadeResponseDto> update(@PathVariable Long id, @RequestBody @Valid PropriedadeRequestDto dto) {
        PropriedadeResponseDto response = propriedadeService.update(id, dto);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propriedadeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinks(PropriedadeResponseDto dto) {
        // Link para obter detalhes da própria propriedade
        dto.add(linkTo(methodOn(PropriedadeController.class).findById(dto.getId())).withSelfRel());
        
        // Link apontando diretamente para o histórico de sensores daquela área (/areas/{id}/sensores)
        dto.add(Link.of("/api/areas/" + dto.getId() + "/sensores").withRel("sensores"));
    }

    private void addSensorHateoasLinks(SensorResponseDto dto) {
        dto.add(linkTo(methodOn(SensorController.class).findById(dto.getId())).withSelfRel());
        dto.add(Link.of("/api/sensores/" + dto.getId() + "/leituras").withRel("leituras"));
        dto.add(linkTo(methodOn(PropriedadeController.class).findById(dto.getPropriedadeId())).withRel("propriedade"));
    }
}
