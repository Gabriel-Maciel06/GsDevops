package com.gs.agroid.controller;

import com.gs.agroid.dto.AlertaRequestDto;
import com.gs.agroid.dto.AlertaResponseDto;
import com.gs.agroid.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @PostMapping
    public ResponseEntity<AlertaResponseDto> create(@RequestBody @Valid AlertaRequestDto dto) {
        AlertaResponseDto response = alertaService.create(dto);
        addHateoasLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AlertaResponseDto>>> findAll(
            Pageable pageable,
            PagedResourcesAssembler<AlertaResponseDto> assembler) {
        Page<AlertaResponseDto> alertas = alertaService.findAll(pageable);
        alertas.forEach(this::addHateoasLinks);
        PagedModel<EntityModel<AlertaResponseDto>> pagedModel = assembler.toModel(alertas);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponseDto> findById(@PathVariable Long id) {
        AlertaResponseDto response = alertaService.findById(id);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/propriedade/{propriedadeId}")
    public ResponseEntity<PagedModel<EntityModel<AlertaResponseDto>>> getAlertasByPropriedade(
            @PathVariable Long propriedadeId,
            Pageable pageable,
            PagedResourcesAssembler<AlertaResponseDto> assembler) {
        Page<AlertaResponseDto> alertas = alertaService.findByPropriedade(propriedadeId, pageable);
        alertas.forEach(this::addHateoasLinks);
        PagedModel<EntityModel<AlertaResponseDto>> pagedModel = assembler.toModel(alertas);
        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertaResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid AlertaRequestDto dto) {
        AlertaResponseDto response = alertaService.update(id, dto);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinks(AlertaResponseDto dto) {
        dto.add(linkTo(methodOn(AlertaController.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(PropriedadeController.class).findById(dto.getPropriedadeId())).withRel("propriedade"));
    }
}
