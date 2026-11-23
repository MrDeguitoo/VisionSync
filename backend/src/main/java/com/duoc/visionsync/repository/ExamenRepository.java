package com.duoc.visionsync.repository;

import com.duoc.visionsync.model.entity.Examen;
import com.duoc.visionsync.model.enum.EstadoExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    @Query("""
        SELECT e FROM Examen e
        JOIN FETCH e.paciente p
        WHERE (:texto IS NULL OR e.tipo ILIKE %:texto% OR p.nombre ILIKE %:texto% OR p.rut ILIKE %:texto%)
        AND (:tipo IS NULL OR e.tipo = :tipo)
        AND (:estado IS NULL OR e.estado = :estado)
        ORDER BY e.fechaHora DESC
        """)
    List<Examen> findWithFilters(
            @Param("texto") String texto,
            @Param("tipo") String tipo,
            @Param("estado") EstadoExamen estado
    );

    List<Examen> findByPacienteId(Long pacienteId);

    Optional<Examen> findByIdWithPaciente(Long id);
}