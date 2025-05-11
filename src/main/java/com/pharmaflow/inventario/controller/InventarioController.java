package com.pharmaflow.inventario.controller;

import com.pharmaflow.inventario.entity.InventarioEntity;
import com.pharmaflow.inventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public List<InventarioEntity> findAll() {
        return inventarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioEntity> findById(@PathVariable Long id) {
        return inventarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InventarioEntity save(@RequestBody InventarioEntity inventario) {
        return inventarioService.save(inventario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioEntity> update(@PathVariable Long id, @RequestBody InventarioEntity updated) {
        return inventarioService.findById(id)
                .map(record -> {
                    record.setNombreProducto(updated.getNombreProducto());
                    record.setDescripcion(updated.getDescripcion());
                    record.setCantidad(updated.getCantidad());
                    record.setPrecioUnitario(updated.getPrecioUnitario());
                    record.setEstado(updated.getEstado());
                    return ResponseEntity.ok(inventarioService.save(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}