package com.duoc.visionsync.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id", nullable = false)
    private Examen examen;

    @NotBlank
    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @NotBlank
    @Column(name = "tipo_mime", nullable = false)
    private String tipoMime;

    @NotBlank
    @Column(name = "ruta", nullable = false)
    private String ruta;

    @Column(name = "descripcion_accesible", length = 500)
    private String descripcionAccesible;

    @CreationTimestamp
    @Column(name = "subido_en", updatable = false)
    private LocalDateTime subidoEn;
}