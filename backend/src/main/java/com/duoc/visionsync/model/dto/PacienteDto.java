package com.duoc.visionsync.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

public class PacienteDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank
        private String rut;

        @NotBlank
        private String nombre;

        @NotNull
        private LocalDate fechaNacimiento;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String rut;
        private String nombre;
        private LocalDate fechaNacimiento;
    }
}