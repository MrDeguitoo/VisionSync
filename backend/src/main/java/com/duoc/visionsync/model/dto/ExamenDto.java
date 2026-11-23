package com.duoc.visionsync.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class ExamenDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank
        private Long pacienteId;

        @NotBlank
        private String tipo;

        private String ojo;

        private String profesional;

        private String sucursal;

        private LocalDateTime fechaHora;

        private String estado;

        private String observaciones;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long pacienteId;
        private String pacienteNombre;
        private String pacienteRut;
        private String tipo;
        private String ojo;
        private String profesional;
        private String sucursal;
        private LocalDateTime fechaHora;
        private String estado;
        private String observaciones;
        private Long documentoId;
        private LocalDateTime actualizadoEn;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class DocumentoUploadRequest {
        private MultipartFile archivo;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DocumentoResponse {
        private Long id;
        private String nombreArchivo;
        private String tipoMime;
        private String descripcionAccesible;
        private LocalDateTime subidoEn;
    }
}