package com.duoc.visionsync.service;

import com.duoc.visionsync.model.entity.Documento;
import com.duoc.visionsync.model.entity.Examen;
import com.duoc.visionsync.repository.DocumentoRepository;
import com.duoc.visionsync.repository.ExamenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final ExamenRepository examenRepository;

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    @Transactional
    public Documento guardar(MultipartFile archivo, Long examenId) {
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() -> new IllegalArgumentException("Examen no encontrado: " + examenId));

        validarArchivo(archivo);

        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadsDir).toAbsolutePath();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear directorio de subidas", e);
        }

        // Generar nombre seguro
        String originalFilename = StringUtils.cleanPath(archivo.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        String storedName = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(storedName);

        try {
            Files.copy(archivo.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }

        Documento documento = Documento.builder()
                .examen(examen)
                .nombreArchivo(originalFilename)
                .tipoMime(archivo.getContentType() != null ? archivo.getContentType() : "application/octet-stream")
                .ruta(targetPath.toString())
                .descripcionAccesible("Archivo adjunto del examen: " + originalFilename)
                .build();

        return documentoRepository.save(documento);
    }

    @Transactional(readOnly = true)
    public Optional<Documento> buscarPorId(Long id) {
        return documentoRepository.findById(id);
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
        }
        String contentType = archivo.getContentType();
        if (contentType == null ||
                !(contentType.equals("application/pdf") ||
                  contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("Solo se permiten PDF e imágenes");
        }
        // Tamaño ya limitado por spring.servlet.multipart.max-file-size (10MB)
    }
}