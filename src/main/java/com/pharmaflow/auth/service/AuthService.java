package com.pharmaflow.auth.service;

import com.pharmaflow.auth.dto.AuthRequestDto;
import com.pharmaflow.auth.dto.AuthResponseDto;
import com.pharmaflow.jwt.JwtService;
import com.pharmaflow.user.entity.UserEntity;
import com.pharmaflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponseDto login(AuthRequestDto request) {
        UserEntity user = userRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getContrasena(), user.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña inválida");
        }

        String token = jwtService.generateToken(user.getCorreo(), user.getRol());

        AuthResponseDto response = new AuthResponseDto();
        response.setCorreo(user.getCorreo());
        response.setRol(user.getRol());
        response.setEstado(user.isEstado());
        response.setToken(token);
        return response;
    }
}