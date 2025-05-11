package com.pharmaflow.roles.repository;

import com.pharmaflow.roles.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByNombre(String nombre);
}