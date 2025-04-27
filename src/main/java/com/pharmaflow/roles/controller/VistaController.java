package com.pharmaflow.roles.controller;

import com.pharmaflow.roles.entity.VistaEntity;
import com.pharmaflow.roles.repository.VistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vistas")
@RequiredArgsConstructor
public class VistaController {

    private final VistaRepository vistaRepository;

    @GetMapping
    public List<VistaEntity> getAll() {
        return vistaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VistaEntity> getById(@PathVariable Long id) {
        return vistaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VistaEntity create(@RequestBody VistaEntity vista) {
        return vistaRepository.save(vista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VistaEntity> update(@PathVariable Long id, @RequestBody VistaEntity updated) {
        return vistaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(updated.getNombre());
                    existing.setRuta(updated.getRuta());
                    return ResponseEntity.ok(vistaRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vistaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
