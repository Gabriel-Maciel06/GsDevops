package com.gs.agroid.controller;

import com.gs.agroid.dto.AtuadorRequestDto;
import com.gs.agroid.dto.AtuadorResponseDto;
import com.gs.agroid.service.AtuadorService;
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
@RequestMapping("/api/atuadores")
@RequiredArgsConstructor
public class AtuadorController {

    private final AtuadorService atuadorService;

    @PostMapping
    public ResponseEntity<AtuadorResponseDto> create(@RequestBody @Valid AtuadorRequestDto dto) {
        AtuadorResponseDto response = atuadorService.create(dto);
        addHateoasLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AtuadorResponseDto>>> findAll(
            Pageable pageable,
            PagedResourcesAssembler<AtuadorResponseDto> assembler) {
        Page<AtuadorResponseDto> atuadores = atuadorService.findAll(pageable);
        atuadores.forEach(this::addHateoasLinks);
        PagedModel<EntityModel<AtuadorResponseDto>> pagedModel = assembler.toModel(atuadores);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtuadorResponseDto> findById(@PathVariable Long id) {
        AtuadorResponseDto response = atuadorService.findById(id);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtuadorResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid AtuadorRequestDto dto) {
        AtuadorResponseDto response = atuadorService.update(id, dto);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<AtuadorResponseDto> toggle(@PathVariable Long id) {
        AtuadorResponseDto response = atuadorService.toggleEstado(id);
        addHateoasLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        atuadorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinks(AtuadorResponseDto dto) {
        dto.add(linkTo(methodOn(AtuadorController.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(PropriedadeController.class).findById(dto.getPropriedadeId())).withRel("propriedade"));
        dto.add(Link.of("/api/atuadores/" + dto.getId() + "/toggle").withRel("toggle"));
    }
}
