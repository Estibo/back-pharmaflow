package com.pharmaflow.BackPharmaFlow.controller;

import com.pharmaflow.BackPharmaFlow.model.entity.Inventario;
import com.pharmaflow.BackPharmaFlow.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioRepository inventarioRepository;

    @GetMapping
    public List<Inventario> listarInventario() {
        return inventarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> agregarProducto(@RequestBody Inventario producto) {
        inventarioRepository.save(producto);
        return ResponseEntity.ok("Producto agregado exitosamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer id, @RequestBody Inventario producto) {
        Inventario existente = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        existente.setNombreProducto(producto.getNombreProducto());
        existente.setDescripcion(producto.getDescripcion());
        existente.setCantidad(producto.getCantidad());
        existente.setPrecioUnitario(producto.getPrecioUnitario());
        inventarioRepository.save(existente);

        return ResponseEntity.ok("Producto actualizado exitosamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        inventarioRepository.deleteById(id);
        return ResponseEntity.ok("Producto eliminado exitosamente");
    }
}
