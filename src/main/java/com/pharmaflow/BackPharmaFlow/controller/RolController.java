package com.pharmaflow.BackPharmaFlow.controller;

import com.pharmaflow.BackPharmaFlow.model.entity.Rol;
import com.pharmaflow.BackPharmaFlow.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @GetMapping
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> agregarRol(@RequestBody Rol rol) {
        if (rolRepository.findAll().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase("Administrador") && rol.getNombre().equalsIgnoreCase("Administrador"))) {
            return ResponseEntity.badRequest().body("El rol 'Administrador' ya existe y no puede ser duplicado.");
        }
        rolRepository.save(rol);
        return ResponseEntity.ok("Rol agregado exitosamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @RequestBody Rol rol) {
        Rol existente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        if ("Administrador".equalsIgnoreCase(existente.getNombre())) {
            return ResponseEntity.badRequest().body("No se puede modificar el rol 'Administrador'");
        }

        existente.setNombre(rol.getNombre());
        existente.setDescripcion(rol.getDescripcion());
        rolRepository.save(existente);

        return ResponseEntity.ok("Rol actualizado exitosamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        if ("Administrador".equalsIgnoreCase(rol.getNombre())) {
            return ResponseEntity.badRequest().body("No se puede eliminar el rol 'Administrador'");
        }

        rolRepository.deleteById(id);
        return ResponseEntity.ok("Rol eliminado exitosamente");
    }
}
