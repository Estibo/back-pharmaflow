package com.pharmaflow.roles.controller;

import com.pharmaflow.roles.entity.RoleEntity;
import com.pharmaflow.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public List<RoleEntity> getAll() {
        return roleRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleEntity> getById(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RoleEntity create(@RequestBody RoleEntity role) {
        return roleRepository.save(role);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleEntity> update(@PathVariable Long id, @RequestBody RoleEntity updated) {
        return roleRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(updated.getNombre());
                    existing.setDescripcion(updated.getDescripcion());
                    return ResponseEntity.ok(roleRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
