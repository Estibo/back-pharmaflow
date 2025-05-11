package com.pharmaflow.BackPharmaFlow.controller;

import com.pharmaflow.BackPharmaFlow.model.entity.Vista;
import com.pharmaflow.BackPharmaFlow.repository.VistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vistas")
public class VistaController {

    @Autowired
    private VistaRepository vistaRepository;

    @GetMapping
    public List<Vista> listarVistas() {
        return vistaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> agregarVista(@RequestBody Vista vista) {
        if (vistaRepository.findAll().stream().anyMatch(v -> v.getRuta().equalsIgnoreCase(vista.getRuta()))) {
            return ResponseEntity.badRequest().body("La ruta de la vista ya existe.");
        }
        vistaRepository.save(vista);
        return ResponseEntity.ok("Vista agregada exitosamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarVista(@PathVariable Integer id, @RequestBody Vista vista) {
        Vista existente = vistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vista no encontrada"));

        existente.setNombre(vista.getNombre());
        existente.setRuta(vista.getRuta());
        vistaRepository.save(existente);

        return ResponseEntity.ok("Vista actualizada exitosamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVista(@PathVariable Integer id) {
        vistaRepository.deleteById(id);
        return ResponseEntity.ok("Vista eliminada exitosamente");
    }
}
