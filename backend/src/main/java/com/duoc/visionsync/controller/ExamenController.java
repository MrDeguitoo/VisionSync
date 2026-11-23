package com.duoc.visionsync.controller;

import com.duoc.visionsync.model.dto.ExamenDto;
import com.duoc.visionsync.service.DocumentoService;
import com.duoc.visionsync.service.ExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/examenes")
@Tag(name = "Exámenes")
@RequiredArgsConstructor
public class ExamenController {

    private final ExamenService examenService;
    private final DocumentoService documentoService;

    @GetMapping
    @Operation(summary = "Listar exámenes con filtros", description = "Filtros: texto (nombre/RUT/tipo), tipo, estado. PACIENTE ve solo sus exámenes.")
    @PreAuthorize("hasAnyRole('TECNOLOGO','MEDICO','PACIENTE')")
    public ResponseEntity<List<ExamenDto.Response>> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            Authentication auth
    ) {
        return ResponseEntity.ok(examenService.listar(texto, tipo, estado, auth));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener examen por ID")
    @PreAuthorize("hasAnyRole('TECNOLOGO','MEDICO','PACIENTE')")
    public ResponseEntity<ExamenDto.Response> buscarPorId(@PathVariable Long id) {
        return examenService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear examen")
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<ExamenDto.Response> crear(@Valid @RequestBody ExamenDto.Request request) {
        return ResponseEntity.status(201).body(examenService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar examen", description = "TECNOLOGO edita todo. MEDICO solo estado y observaciones.")
    @PreAuthorize("hasAnyRole('TECNOLOGO','MEDICO')")
    public ResponseEntity<ExamenDto.Response> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExamenDto.Request request,
            Authentication auth
    ) {
        return ResponseEntity.ok(examenService.actualizar(id, request, auth));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar examen")
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        examenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/documento")
    @Operation(summary = "Subir documento (PDF o imagen) a un examen")
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<ExamenDto.DocumentoResponse> subirDocumento(
            @PathVariable Long id,
            @RequestPart("archivo") MultipartFile archivo
    ) {
        var documento = documentoService.guardar(archivo, id);
        examenService.vincularDocumento(id, documento.getId());
        return ResponseEntity.status(201).body(toDocumentoResponse(documento));
    }

    @GetMapping("/documentos/{id}/archivo")
    @Operation(summary = "Descargar documento de un examen")
    @PreAuthorize("hasAnyRole('TECNOLOGO','MEDICO','PACIENTE')")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id, HttpServletRequest request) {
        var documento = documentoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + id));

        Path path = Paths.get(documento.getRuta()).toAbsolutePath();
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error al leer archivo", e);
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Archivo no encontrado o no legible");
        }

        MediaType mediaType = MediaType.parseMediaType(documento.getTipoMime());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + documento.getNombreArchivo() + "\"")
                .body(resource);
    }

    private ExamenDto.DocumentoResponse toDocumentoResponse(com.duoc.visionsync.model.entity.Documento d) {
        return ExamenDto.DocumentoResponse.builder()
                .id(d.getId())
                .nombreArchivo(d.getNombreArchivo())
                .tipoMime(d.getTipoMime())
                .descripcionAccesible(d.getDescripcionAccesible())
                .subidoEn(d.getSubidoEn())
                .build();
    }
}