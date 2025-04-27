package com.pharmaflow.inventario.repository;

import com.pharmaflow.inventario.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {
}
