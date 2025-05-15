package com.pharmaflow.BackPharmaFlow.controller;

import com.pharmaflow.BackPharmaFlow.model.entity.Usuario;
import com.pharmaflow.BackPharmaFlow.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/admin")
    public ResponseEntity<?> createAdminUser() {
        // Verificar si ya existe un admin
        if (usuarioRepository.findByCorreo("admin@pharmaflow.com").isPresent()) {
            return ResponseEntity.badRequest().body("El usuario administrador ya existe");
        }

        Usuario admin = new Usuario();
        admin.setCedula("1234567890");
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setCorreo("admin@pharmaflow.com");
        admin.setContrasena(passwordEncoder.encode("admin123")); // Contraseña hasheada
        admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        admin.setCiudadNacimiento("Ciudad Predeterminada");
        admin.setCiudadResidencia("Ciudad Predeterminada");
        admin.setRol("Administrador");
        admin.setEstado(true);
        admin.setFechaCreacion(LocalDateTime.now());
        admin.setIntentosLogin(0);

        usuarioRepository.save(admin);
        return ResponseEntity.ok("Usuario administrador creado exitosamente");
    }
}
