package com.pharmaflow.inventario.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class InventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreProducto;
    private String descripcion;
    private Integer cantidad;
    private Double precioUnitario;
    private Boolean estado;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;
}