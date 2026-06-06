package com.gs.agroid.controller;

import com.gs.agroid.dto.PropriedadeRequestDto;
import com.gs.agroid.dto.PropriedadeResponseDto;
import com.gs.agroid.dto.SensorResponseDto;
import com.gs.agroid.dto.AtuadorResponseDto;
import com.gs.agroid.service.PropriedadeService;
import com.gs.agroid.service.SensorService;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class PropriedadeController {

    private final PropriedadeService propriedadeService;
    private final SensorService sensorService;
    private final AtuadorService atuadorService;

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

    // Endpoint para retornar os atuadores daquela área
    @GetMapping("/{id}/atuadores")
    public ResponseEntity<List<AtuadorResponseDto>> getAtuadoresByArea(@PathVariable Long id) {
        List<AtuadorResponseDto> atuadores = atuadorService.findByPropriedade(id);
        atuadores.forEach(this::addAtuadorHateoasLinks);
        return ResponseEntity.ok(atuadores);
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
        
        // Link apontando diretamente para os sensores daquela área (/areas/{id}/sensores)
        dto.add(linkTo(methodOn(PropriedadeController.class).getSensoresByArea(dto.getId())).withRel("sensores"));

        // Link apontando diretamente para os atuadores daquela área (/areas/{id}/atuadores)
        dto.add(linkTo(methodOn(PropriedadeController.class).getAtuadoresByArea(dto.getId())).withRel("atuadores"));
    }

    private void addSensorHateoasLinks(SensorResponseDto dto) {
        dto.add(linkTo(methodOn(SensorController.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(SensorController.class).getLeituras(dto.getId(), null, null)).withRel("leituras"));
        dto.add(linkTo(methodOn(PropriedadeController.class).findById(dto.getPropriedadeId())).withRel("propriedade"));
    }

    private void addAtuadorHateoasLinks(AtuadorResponseDto dto) {
        dto.add(linkTo(methodOn(AtuadorController.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(PropriedadeController.class).findById(dto.getPropriedadeId())).withRel("propriedade"));
        dto.add(linkTo(methodOn(AtuadorController.class).toggle(dto.getId())).withRel("toggle"));
    }
}
