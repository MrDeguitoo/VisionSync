package com.duoc.visionsync.controller;

import com.duoc.visionsync.model.dto.PacienteDto;
import com.duoc.visionsync.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    @Operation(summary = "Listar pacientes")
    @PreAuthorize("hasAnyRole('TECNOLOGO','MEDICO')")
    public ResponseEntity<List<PacienteDto.Response>> listar() {
        return ResponseEntity.ok(pacienteService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID")
    @PreAuthorize("hasAnyRole('TECNOLOGO','MEDICO','PACIENTE')")
    public ResponseEntity<PacienteDto.Response> buscarPorId(@PathVariable Long id) {
        return pacienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear paciente")
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<PacienteDto.Response> crear(@Valid @RequestBody PacienteDto.Request request) {
        return ResponseEntity.status(201).body(pacienteService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar paciente")
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<PacienteDto.Response> actualizar(@PathVariable Long id, @Valid @RequestBody PacienteDto.Request request) {
        return ResponseEntity.ok(pacienteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar paciente")
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}