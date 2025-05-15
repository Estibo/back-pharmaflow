package com.pharmaflow.BackPharmaFlow.repository;

import com.pharmaflow.BackPharmaFlow.model.entity.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Integer> {
    void deleteByCarritoId(Integer carritoId);
}
