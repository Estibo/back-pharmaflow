package com.pharmaflow.BackPharmaFlow.validation;

import com.pharmaflow.BackPharmaFlow.exceptions.VentaException;
import com.pharmaflow.BackPharmaFlow.model.entity.Carrito;
import com.pharmaflow.BackPharmaFlow.model.entity.ItemCarrito;
import com.pharmaflow.BackPharmaFlow.model.entity.Inventario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CarritoValidator {

    public void validarStock(Inventario producto, int cantidadSolicitada) {
        if (producto.getCantidad() < cantidadSolicitada) {
            throw VentaException.stockInsuficiente(producto.getNombreProducto());
        }
    }

    public void validarCarritoActivo(Carrito carrito) {
        if (!carrito.getEstado()) {
            throw new VentaException("Este carrito ya no está activo", "CARRITO_INACTIVO");
        }

        if (carrito.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new VentaException("El carrito ha expirado", "CARRITO_EXPIRADO");
        }
    }

    public void validarLimiteProductos(Carrito carrito) {
        if (carrito.getItems().size() >= 20) {
            throw new VentaException(
                "Se ha alcanzado el límite máximo de productos diferentes en el carrito",
                "LIMITE_PRODUCTOS"
            );
        }
    }

    public void validarLimiteCantidad(ItemCarrito item) {
        if (item.getCantidad() > 100) {
            throw new VentaException(
                "La cantidad máxima permitida por producto es 100 unidades",
                "LIMITE_CANTIDAD"
            );
        }
    }

    public void validarTotalCarrito(Carrito carrito) {
        double total = carrito.getItems().stream()
            .mapToDouble(item -> 
                item.getPrecioUnitario().doubleValue() * item.getCantidad()
            )
            .sum();

        if (total > 10000000) {  // 10 millones (ajustar según necesidades)
            throw new VentaException(
                "El monto total del carrito excede el límite permitido",
                "LIMITE_MONTO"
            );
        }
    }
}
