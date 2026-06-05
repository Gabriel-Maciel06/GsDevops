package com.gs.agroid.controller;

import com.gs.agroid.dto.SateliteDadosRequestDto;
import com.gs.agroid.dto.SateliteDadosResponseDto;
import com.gs.agroid.service.SateliteDadosService;
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
@RequestMapping("/api/satelite")
@RequiredArgsConstructor
public class SateliteDadosController {

    private final SateliteDadosService sateliteDadosService;

    @PostMapping
    public ResponseEntity<SateliteDadosResponseDto> create(@RequestBody @Valid SateliteDadosRequestDto dto) {
        SateliteDadosResponseDto response = sateliteDadosService.create(dto);
        addHateoasLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<SateliteDadosResponseDto>>> findAll(
            Pageable pageable,
            PagedResourcesAssembler<SateliteDadosResponseDto> assembler) {
        Page<SateliteDadosResponseDto> dados = sateliteDadosService.findAll(pageable);
        dados.forEach(this::addHateoasLinks);
        PagedModel<EntityModel<SateliteDadosResponseDto>> pagedModel = assembler.toModel(dados);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SateliteDadosResponseDto> findById(@PathVariable Long id) {
        SateliteDadosResponseDto response = sateliteDadosService.findById(id);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/regiao/{regiao}")
    public ResponseEntity<PagedModel<EntityModel<SateliteDadosResponseDto>>> getByRegiao(
            @PathVariable String regiao,
            Pageable pageable,
            PagedResourcesAssembler<SateliteDadosResponseDto> assembler) {
        Page<SateliteDadosResponseDto> dados = sateliteDadosService.findByRegiao(regiao, pageable);
        dados.forEach(this::addHateoasLinks);
        PagedModel<EntityModel<SateliteDadosResponseDto>> pagedModel = assembler.toModel(dados);
        return ResponseEntity.ok(pagedModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SateliteDadosResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid SateliteDadosRequestDto dto) {
        SateliteDadosResponseDto response = sateliteDadosService.update(id, dto);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sateliteDadosService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinks(SateliteDadosResponseDto dto) {
        dto.add(linkTo(methodOn(SateliteDadosController.class).findById(dto.getId())).withSelfRel());
    }
}
