package com.duoc.visionsync.model.entity;

import com.duoc.visionsync.model.enum.EstadoExamen;
import com.duoc.visionsync.model.enum.Ojo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "examenes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @NotBlank
    @Column(nullable = false)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ojo ojo;

    private String profesional;

    private String sucursal;

    @NotNull
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoExamen estado = EstadoExamen.PENDIENTE;

    @Column(length = 1000)
    private String observaciones = "";

    @Column(name = "documento_id")
    private Long documentoId;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}