package com.pharmaflow.BackPharmaFlow.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @Column(name = "numero_factura", length = 50)
    private String numeroFactura;

    @ManyToOne
    @JoinColumn(name = "cedula_cliente", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "cedula_empleado", nullable = false)
    private Usuario empleado;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta = LocalDateTime.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @PrePersist
    protected void onCreate() {
        String fecha = fechaVenta.toString().replace("-", "").replace(":", "").replace(".", "");
        numeroFactura = "FAC-" + fecha;
    }
}
