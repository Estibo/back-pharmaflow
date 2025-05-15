package com.pharmaflow.BackPharmaFlow.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @Column(nullable = false, length = 20)
    private String cedula;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 100)
    private String ciudadNacimiento;

    @Column(nullable = false, length = 100)
    private String ciudadResidencia;

    @Column(nullable = false, length = 50)
    private String rol;

    @Column(length = 100)
    private String cargo;

    @Column(length = 100)
    private String area;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(name = "intentos_login")
    private Integer intentosLogin = 0;

    @Column(name = "ultimo_intento_fallido")
    private LocalDateTime ultimoIntentoFallido;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
