package com.pharmaflow.BackPharmaFlow.controller;

import com.pharmaflow.BackPharmaFlow.model.entity.Usuario;
import com.pharmaflow.BackPharmaFlow.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }    @PutMapping("/{cedula}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable String cedula, @RequestBody Usuario usuario) {
        Usuario existente = usuarioRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setCorreo(usuario.getCorreo());
        existente.setFechaNacimiento(usuario.getFechaNacimiento());
        existente.setCiudadNacimiento(usuario.getCiudadNacimiento());
        existente.setCiudadResidencia(usuario.getCiudadResidencia());
        existente.setRol(usuario.getRol());
        existente.setCargo(usuario.getCargo());
        existente.setArea(usuario.getArea());
        usuarioRepository.save(existente);

        return ResponseEntity.ok("Usuario actualizado exitosamente");
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String cedula) {
        Usuario usuario = usuarioRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if ("Administrador".equalsIgnoreCase(usuario.getRol())) {
            return ResponseEntity.badRequest().body("No se puede eliminar el rol Administrador");
        }        usuarioRepository.deleteById(cedula);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }
}
