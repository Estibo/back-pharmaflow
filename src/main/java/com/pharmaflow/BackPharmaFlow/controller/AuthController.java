package com.pharmaflow.BackPharmaFlow.controller;

import com.pharmaflow.BackPharmaFlow.model.entity.Usuario;
import com.pharmaflow.BackPharmaFlow.repository.UsuarioRepository;
import com.pharmaflow.BackPharmaFlow.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioRepository.findByCorreo(loginRequest.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getEstado()) {
            return ResponseEntity.badRequest().body("Usuario inactivo");
        }

        if (usuario.getIntentosLogin() >= 3 && 
            usuario.getUltimoIntentoFallido() != null && 
            usuario.getUltimoIntentoFallido().plusMinutes(15).isAfter(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Cuenta bloqueada. Intente nuevamente en 15 minutos");
        }

        if (!passwordEncoder.matches(loginRequest.getContrasena(), usuario.getContrasena())) {
            usuario.setIntentosLogin(usuario.getIntentosLogin() + 1);
            if (usuario.getIntentosLogin() >= 3) {
                usuario.setUltimoIntentoFallido(java.time.LocalDateTime.now());
            }
            usuarioRepository.save(usuario);
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        }

        usuario.setIntentosLogin(0);
        usuario.setUltimoIntentoFallido(null);
        usuarioRepository.save(usuario);

        String token = jwtTokenProvider.createToken(usuario.getCorreo(), Collections.singletonList(usuario.getRol()));
        return ResponseEntity.ok(new LoginResponse(token, usuario.getRol()));
    }    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        // Validar campos requeridos
        if (usuario.getCedula() == null || usuario.getNombre() == null || usuario.getApellido() == null ||
            usuario.getCorreo() == null || usuario.getContrasena() == null || usuario.getFechaNacimiento() == null ||
            usuario.getCiudadNacimiento() == null || usuario.getCiudadResidencia() == null) {
            return ResponseEntity.badRequest().body("Todos los campos son requeridos");
        }

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        // Por defecto, asignar rol "Usuario" si no se especifica
        if (usuario.getRol() == null) {
            usuario.setRol("Usuario");
        }

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setEstado(true);
        usuario.setIntentosLogin(0);
        usuario.setFechaCreacion(java.time.LocalDateTime.now());
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    static class LoginRequest {
        private String correo;
        private String contrasena;

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }    static class LoginResponse {
        private String token;
        private String rol;

        public LoginResponse(String token, String rol) {
            this.token = token;
            this.rol = rol;
        }

        public String getToken() {
            return token;
        }

        public String getRol() {
            return rol;
        }

        // Getter...
    }
}
