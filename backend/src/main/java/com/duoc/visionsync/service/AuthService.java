package com.duoc.visionsync.service;

import com.duoc.visionsync.model.dto.AuthDtos;
import com.duoc.visionsync.model.entity.Usuario;
import com.duoc.visionsync.repository.UsuarioRepository;
import com.duoc.visionsync.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getNombre(),
                usuario.getPacienteId()
        );

        return AuthDtos.LoginResponse.builder()
                .token(token)
                .rol(usuario.getRol().name())
                .nombre(usuario.getNombre())
                .pacienteId(usuario.getPacienteId())
                .build();
    }
}