package com.pharmaflow.BackPharmaFlow.repository;

import com.pharmaflow.BackPharmaFlow.model.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findByUsuarioCedulaAndEstadoTrue(String cedula);
    List<Carrito> findByFechaExpiracionLessThanAndEstadoTrue(LocalDateTime fecha);
}
