package com.duoc.visionsync.service;

import com.duoc.visionsync.model.dto.PacienteDto;
import com.duoc.visionsync.model.entity.Paciente;
import com.duoc.visionsync.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public List<PacienteDto.Response> listar() {
        return pacienteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<PacienteDto.Response> buscarPorId(Long id) {
        return pacienteRepository.findById(id).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<PacienteDto.Response> buscarPorRut(String rut) {
        return pacienteRepository.findByRut(rut).map(this::toResponse);
    }

    @Transactional
    public PacienteDto.Response crear(PacienteDto.Request request) {
        Paciente paciente = Paciente.builder()
                .rut(request.getRut())
                .nombre(request.getNombre())
                .fechaNacimiento(request.getFechaNacimiento())
                .build();
        return toResponse(pacienteRepository.save(paciente));
    }

    @Transactional
    public PacienteDto.Response actualizar(Long id, PacienteDto.Request request) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado: " + id));
        paciente.setRut(request.getRut());
        paciente.setNombre(request.getNombre());
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        return toResponse(pacienteRepository.save(paciente));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente no encontrado: " + id);
        }
        pacienteRepository.deleteById(id);
    }

    private PacienteDto.Response toResponse(Paciente p) {
        return PacienteDto.Response.builder()
                .id(p.getId())
                .rut(p.getRut())
                .nombre(p.getNombre())
                .fechaNacimiento(p.getFechaNacimiento())
                .build();
    }
}