package com.pharmaflow.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {
    private String correo;
    private String token;
    private String rol;
    private boolean estado;
}