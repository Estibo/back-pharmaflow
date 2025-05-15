package com.pharmaflow.BackPharmaFlow.controller;

import com.pharmaflow.BackPharmaFlow.model.entity.*;
import com.pharmaflow.BackPharmaFlow.repository.*;
import com.pharmaflow.BackPharmaFlow.exceptions.VentaException;
import com.pharmaflow.BackPharmaFlow.validation.CarritoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private CarritoValidator carritoValidator;

    @GetMapping
    public ResponseEntity<?> obtenerCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        Carrito carrito = obtenerOCrearCarrito(usuario);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@RequestBody AgregarProductoRequest request) {
        Usuario usuario = obtenerUsuarioActual();
        Carrito carrito = obtenerOCrearCarrito(usuario);

        Inventario producto = inventarioRepository.findById(request.getProductoId())
            .orElseThrow(() -> new VentaException("Producto no encontrado", "PRODUCTO_NO_ENCONTRADO"));        // Validaciones
        carritoValidator.validarCarritoActivo(carrito);
        carritoValidator.validarStock(producto, request.getCantidad());
        carritoValidator.validarLimiteProductos(carrito);

        // Verificar si el producto ya está en el carrito
        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
            .filter(item -> item.getProducto().getId().equals(request.getProductoId()))
            .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + request.getCantidad();
            
            if (nuevaCantidad > producto.getCantidad()) {
                throw VentaException.stockInsuficiente(producto.getNombreProducto());
            }
            
            item.setCantidad(nuevaCantidad);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setProducto(producto);            nuevoItem.setCantidad(request.getCantidad());
            nuevoItem.setPrecioUnitario(producto.getPrecioUnitario());
            nuevoItem.setCarrito(carrito);
            carrito.getItems().add(nuevoItem);
            
            // Validar límites después de agregar/actualizar
            carritoValidator.validarLimiteCantidad(nuevoItem);
            carritoValidator.validarTotalCarrito(carrito);
        }

        carritoRepository.save(carrito);
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Integer itemId,
            @RequestBody ActualizarCantidadRequest request) {
        
        Usuario usuario = obtenerUsuarioActual();
        Carrito carrito = obtenerCarritoActivo(usuario);

        ItemCarrito item = carrito.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new VentaException("Item no encontrado", "ITEM_NO_ENCONTRADO"));

        if (request.getCantidad() > item.getProducto().getCantidad()) {
            throw VentaException.stockInsuficiente(item.getProducto().getNombreProducto());
        }

        item.setCantidad(request.getCantidad());
        carritoRepository.save(carrito);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> eliminarItem(@PathVariable Integer itemId) {
        Usuario usuario = obtenerUsuarioActual();
        Carrito carrito = obtenerCarritoActivo(usuario);

        carrito.getItems().removeIf(item -> item.getId().equals(itemId));
        carritoRepository.save(carrito);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping
    public ResponseEntity<?> limpiarCarrito() {
        Usuario usuario = obtenerUsuarioActual();
        Carrito carrito = obtenerCarritoActivo(usuario);

        carrito.getItems().clear();
        carritoRepository.save(carrito);
        return ResponseEntity.ok().build();
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByCorreo(auth.getName())
            .orElseThrow(() -> new VentaException("Usuario no encontrado", "USUARIO_NO_ENCONTRADO"));
    }

    private Carrito obtenerOCrearCarrito(Usuario usuario) {
        return carritoRepository.findByUsuarioCedulaAndEstadoTrue(usuario.getCedula())
            .orElseGet(() -> {
                Carrito nuevoCarrito = new Carrito();
                nuevoCarrito.setUsuario(usuario);
                nuevoCarrito.setFechaCreacion(LocalDateTime.now());
                nuevoCarrito.setFechaExpiracion(LocalDateTime.now().plusDays(1));
                nuevoCarrito.setEstado(true);
                return carritoRepository.save(nuevoCarrito);
            });
    }

    private Carrito obtenerCarritoActivo(Usuario usuario) {
        return carritoRepository.findByUsuarioCedulaAndEstadoTrue(usuario.getCedula())
            .orElseThrow(() -> VentaException.carritoNoEncontrado());
    }

    @Getter @Setter
    static class AgregarProductoRequest {
        private Integer productoId;
        private Integer cantidad;
    }

    @Getter @Setter
    static class ActualizarCantidadRequest {
        private Integer cantidad;
    }
}
