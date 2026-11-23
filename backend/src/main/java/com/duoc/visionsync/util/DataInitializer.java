package com.duoc.visionsync.util;

import com.duoc.visionsync.model.entity.*;
import com.duoc.visionsync.model.enum.EstadoExamen;
import com.duoc.visionsync.model.enum.Ojo;
import com.duoc.visionsync.model.enum.Rol;
import com.duoc.visionsync.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final ExamenRepository examenRepository;
    private final DocumentoRepository documentoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return; // Ya hay datos, no inicializar
        }

        // 1. Usuarios semilla
        Usuario tecnologo = Usuario.builder()
                .email("tecnologo@visionsync.test")
                .passwordHash(passwordEncoder.encode("demo1234"))
                .nombre("Tecnólogo Médico Demo")
                .rol(Rol.TECNOLOGO)
                .build();

        Usuario medico = Usuario.builder()
                .email("medico@visionsync.test")
                .passwordHash(passwordEncoder.encode("demo1234"))
                .nombre("Dr. Médico Oftalmólogo Demo")
                .rol(Rol.MEDICO)
                .build();

        // Pacientes
        Paciente paciente1 = Paciente.builder()
                .rut("11111111-1")
                .nombre("Paciente Ficticio Uno")
                .fechaNacimiento(LocalDate.of(1980, 5, 15))
                .build();

        Paciente paciente2 = Paciente.builder()
                .rut("22222222-2")
                .nombre("Paciente Ficticio Dos")
                .fechaNacimiento(LocalDate.of(1990, 8, 22))
                .build();

        Paciente paciente3 = Paciente.builder()
                .rut("33333333-3")
                .nombre("Paciente Ficticio Tres")
                .fechaNacimiento(LocalDate.of(1975, 3, 10))
                .build();

        pacienteRepository.saveAll(List.of(paciente1, paciente2, paciente3));

        // Usuario paciente vinculado a paciente1
        Usuario pacienteUser = Usuario.builder()
                .email("paciente@visionsync.test")
                .passwordHash(passwordEncoder.encode("demo1234"))
                .nombre("Paciente Ficticio Uno")
                .rol(Rol.PACIENTE)
                .pacienteId(paciente1.getId())
                .build();

        usuarioRepository.saveAll(List.of(tecnologo, medico, pacienteUser));

        // 2. Exámenes de prueba
        var examenes = List.of(
                Examen.builder()
                        .paciente(paciente1)
                        .tipo("Campimetría Computarizada")
                        .ojo(Ojo.OD)
                        .profesional("Dra. Ficticia")
                        .sucursal("Sucursal Centro")
                        .fechaHora(LocalDateTime.now().minusDays(5))
                        .estado(EstadoExamen.AUTORIZADO)
                        .observaciones("Campo visual dentro de límites normales. Sin defectos significativos.")
                        .build(),
                Examen.builder()
                        .paciente(paciente1)
                        .tipo("Tomografía de Coherencia Óptica (OCT)")
                        .ojo(Ojo.OI)
                        .profesional("Dr. Ficticio")
                        .sucursal("Sucursal Norte")
                        .fechaHora(LocalDateTime.now().minusDays(3))
                        .estado(EstadoExamen.AUTORIZADO)
                        .observaciones("RNFL preservado. No hay signos de glaucomatopía.")
                        .build(),
                Examen.builder()
                        .paciente(paciente2)
                        .tipo("Topografía Corneal")
                        .ojo(Ojo.AO)
                        .profesional("Dra. Ficticia")
                        .sucursal("Sucursal Centro")
                        .fechaHora(LocalDateTime.now().minusDays(1))
                        .estado(EstadoExamen.REVISADO)
                        .observaciones("Astigmatismo regular. Revisar en control.")
                        .build(),
                Examen.builder()
                        .paciente(paciente2)
                        .tipo("Tonometría y Curva de Tensión")
                        .ojo(Ojo.OD)
                        .profesional("Dr. Ficticio")
                        .sucursal("Sucursal Sur")
                        .fechaHora(LocalDateTime.now())
                        .estado(EstadoExamen.PENDIENTE)
                        .observaciones("")
                        .build(),
                Examen.builder()
                        .paciente(paciente3)
                        .tipo("Agudeza Visual y Refracción")
                        .ojo(Ojo.AO)
                        .profesional("Dra. Ficticia")
                        .sucursal("Sucursal Centro")
                        .fechaHora(LocalDateTime.now().minusDays(10))
                        .estado(EstadoExamen.AUTORIZADO)
                        .observaciones("AV: OD 20/25, OI 20/30. Refracción: OD -1.25/-0.50x180, OI -1.50/-0.75x170")
                        .build()
        );

        examenRepository.saveAll(examenes);

        // 3. Documentos sintéticos
        crearDocumentosSinteticos(examenes);
    }

    private void crearDocumentosSinteticos(List<Examen> examenes) {
        Path uploadPath = Paths.get(uploadsDir).toAbsolutePath();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Error creando directorio uploads", e);
        }

        // PDF sintético simple
        byte[] pdfContent = generarPdfSintetico();
        // Imagen sintética (PNG 1x1 pixel)
        byte[] pngContent = generarPngSintetico();

        for (int i = 0; i < examenes.size(); i++) {
            Examen examen = examenes.get(i);
            boolean esPdf = i % 2 == 0;

            byte[] content = esPdf ? pdfContent : pngContent;
            String extension = esPdf ? ".pdf" : ".png";
            String mimeType = esPdf ? "application/pdf" : "image/png";
            String storedName = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(storedName);
            try {
                Files.write(targetPath, content);
            } catch (IOException e) {
                throw new RuntimeException("Error escribiendo archivo sintético", e);
            }

            Documento documento = Documento.builder()
                    .examen(examen)
                    .nombreArchivo("informe_" + examen.getTipo().replace(" ", "_") + extension)
                    .tipoMime(mimeType)
                    .ruta(targetPath.toString())
                    .descripcionAccesible("Informe de " + examen.getTipo() + " para " + examen.getPaciente().getNombre())
                    .build();

            documentoRepository.save(documento);
            examen.setDocumentoId(documento.getId());
        }
    }

    private byte[] generarPdfSintetico() {
        // PDF mínimo válido
        return "%PDF-1.4\n1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R >>\nendobj\n4 0 obj\n<< /Length 44 >>\nstream\nBT /F1 12 Tf 100 700 Td (Informe VisionSync - Datos ficticios) Tj ET\nendstream\nendobj\nxref\n0 5\n0000000000 65535 f \n0000000009 00000 n \n0000000058 00000 n \n0000000115 00000 n \n0000000216 00000 n \ntrailer\n<< /Size 5 /Root 1 0 R >>\nstartxref\n312\n%%EOF".getBytes();
    }

    private byte[] generarPngSintetico() {
        // PNG 1x1 pixel transparente (base64 decoded)
        return java.util.Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
        );
    }
}