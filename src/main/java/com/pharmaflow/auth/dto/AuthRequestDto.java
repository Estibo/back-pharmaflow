package com.pharmaflow.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    private String correo;
    private String contrasena;
}