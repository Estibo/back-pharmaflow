package com.pharmaflow.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secret;

    public String generateToken(String correo, String rol) {
        return JWT.create()
                .withSubject(correo)
                .withClaim("rol", rol)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                .sign(Algorithm.HMAC256(secret));
    }

    public String getCorreoFromToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getSubject();
    }
}
