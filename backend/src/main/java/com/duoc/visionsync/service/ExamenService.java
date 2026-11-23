package com.duoc.visionsync.service;

import com.duoc.visionsync.model.dto.ExamenDto;
import com.duoc.visionsync.model.entity.Examen;
import com.duoc.visionsync.model.entity.Paciente;
import com.duoc.visionsync.model.enum.EstadoExamen;
import com.duoc.visionsync.repository.ExamenRepository;
import com.duoc.visionsync.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamenService {

    private final ExamenRepository examenRepository;
    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public List<ExamenDto.Response> listar(String texto, String tipo, String estado, Authentication auth) {
        Long pacienteId = getPacienteIdFromAuth(auth);
        String rol = auth != null ? auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "") : null;

        EstadoExamen estadoEnum = estado != null ? EstadoExamen.valueOf(estado) : null;

        List<Examen> examenes;
        if ("PACIENTE".equals(rol) && pacienteId != null) {
            examenes = examenRepository.findByPacienteId(pacienteId);
        } else {
            examenes = examenRepository.findWithFilters(texto, tipo, estadoEnum);
        }

        return examenes.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ExamenDto.Response> buscarPorId(Long id) {
        return examenRepository.findByIdWithPaciente(id).map(this::toResponse);
    }

    @Transactional
    public ExamenDto.Response crear(ExamenDto.Request request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado: " + request.getPacienteId()));

        Examen examen = Examen.builder()
                .paciente(paciente)
                .tipo(request.getTipo())
                .ojo(request.getOjo() != null ? com.duoc.visionsync.model.enum.Ojo.valueOf(request.getOjo()) : null)
                .profesional(request.getProfesional())
                .sucursal(request.getSucursal())
                .fechaHora(request.getFechaHora() != null ? request.getFechaHora() : LocalDateTime.now())
                .estado(EstadoExamen.PENDIENTE)
                .observaciones(request.getObservaciones() != null ? request.getObservaciones() : "")
                .build();

        return toResponse(examenRepository.save(examen));
    }

    @Transactional
    public ExamenDto.Response actualizar(Long id, ExamenDto.Request request, Authentication auth) {
        Examen examen = examenRepository.findByIdWithPaciente(id)
                .orElseThrow(() -> new IllegalArgumentException("Examen no encontrado: " + id));

        String rol = auth != null ? auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "") : null;

        if ("TECNOLOGO".equals(rol)) {
            // Tecnólogo puede editar todo
            if (request.getPacienteId() != null) {
                Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                        .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado: " + request.getPacienteId()));
                examen.setPaciente(paciente);
            }
            if (request.getTipo() != null) examen.setTipo(request.getTipo());
            if (request.getOjo() != null) examen.setOjo(com.duoc.visionsync.model.enum.Ojo.valueOf(request.getOjo()));
            if (request.getProfesional() != null) examen.setProfesional(request.getProfesional());
            if (request.getSucursal() != null) examen.setSucursal(request.getSucursal());
            if (request.getFechaHora() != null) examen.setFechaHora(request.getFechaHora());
        }

        // Médico y Tecnólogo pueden cambiar estado y observaciones
        if (request.getEstado() != null) examen.setEstado(EstadoExamen.valueOf(request.getEstado()));
        if (request.getObservaciones() != null) examen.setObservaciones(request.getObservaciones());

        return toResponse(examenRepository.save(examen));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!examenRepository.existsById(id)) {
            throw new IllegalArgumentException("Examen no encontrado: " + id);
        }
        examenRepository.deleteById(id);
    }

    @Transactional
    public ExamenDto.Response vincularDocumento(Long examenId, Long documentoId) {
        Examen examen = examenRepository.findByIdWithPaciente(examenId)
                .orElseThrow(() -> new IllegalArgumentException("Examen no encontrado: " + examenId));
        examen.setDocumentoId(documentoId);
        return toResponse(examenRepository.save(examen));
    }

    private ExamenDto.Response toResponse(Examen e) {
        return ExamenDto.Response.builder()
                .id(e.getId())
                .pacienteId(e.getPaciente().getId())
                .pacienteNombre(e.getPaciente().getNombre())
                .pacienteRut(e.getPaciente().getRut())
                .tipo(e.getTipo())
                .ojo(e.getOjo() != null ? e.getOjo().name() : null)
                .profesional(e.getProfesional())
                .sucursal(e.getSucursal())
                .fechaHora(e.getFechaHora())
                .estado(e.getEstado().name())
                .observaciones(e.getObservaciones())
                .documentoId(e.getDocumentoId())
                .actualizadoEn(e.getActualizadoEn())
                .build();
    }

    private Long getPacienteIdFromAuth(Authentication auth) {
        if (auth == null) return null;
        return (Long) auth.getDetails(); // Not used, we use request attribute
    }
}