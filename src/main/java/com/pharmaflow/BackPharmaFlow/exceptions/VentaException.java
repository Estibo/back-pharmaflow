package com.pharmaflow.BackPharmaFlow.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VentaException extends RuntimeException {
    private final String code;

    public VentaException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VentaException stockInsuficiente(String nombreProducto) {
        return new VentaException(
            "Stock insuficiente para " + nombreProducto,
            "STOCK_INSUFICIENTE"
        );
    }

    public static VentaException carritoVacio() {
        return new VentaException(
            "El carrito está vacío",
            "CARRITO_VACIO"
        );
    }

    public static VentaException carritoNoEncontrado() {
        return new VentaException(
            "No se encontró un carrito activo para el usuario",
            "CARRITO_NO_ENCONTRADO"
        );
    }

    public static VentaException usuarioNoEncontrado() {
        return new VentaException(
            "Usuario no encontrado",
            "USUARIO_NO_ENCONTRADO"
        );
    }
}
