    package com.pharmaflow.inventario.service;

    import com.pharmaflow.inventario.entity.InventarioEntity;
    import com.pharmaflow.inventario.repository.InventarioRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;

    @Service
    @RequiredArgsConstructor
    public class InventarioService {

        private final InventarioRepository inventarioRepository;

        public List<InventarioEntity> findAll() {
            return inventarioRepository.findAll();
        }

        public Optional<InventarioEntity> findById(Long id) {
            return inventarioRepository.findById(id);
        }

        public InventarioEntity save(InventarioEntity inventario) {
            return inventarioRepository.save(inventario);
        }

        public void delete(Long id) {
            inventarioRepository.deleteById(id);
        }
    }
