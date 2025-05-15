package com.pharmaflow.BackPharmaFlow.repository;

import com.pharmaflow.BackPharmaFlow.model.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, String> {
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByClienteCedula(String cedula);
    List<Venta> findByEmpleadoCedula(String cedula);
}
