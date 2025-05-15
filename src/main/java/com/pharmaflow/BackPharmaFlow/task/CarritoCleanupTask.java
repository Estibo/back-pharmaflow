package com.pharmaflow.BackPharmaFlow.task;

import com.pharmaflow.BackPharmaFlow.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class CarritoCleanupTask {

    @Autowired
    private CarritoRepository carritoRepository;

    @Scheduled(cron = "0 0 */2 * * *") // Cada 2 horas
    @Transactional
    public void limpiarCarritosExpirados() {
        LocalDateTime now = LocalDateTime.now();
        
        carritoRepository.findAll().stream()
            .filter(carrito -> 
                carrito.getEstado() && 
                carrito.getFechaExpiracion().isBefore(now)
            )
            .forEach(carrito -> {
                carrito.setEstado(false);
                carritoRepository.save(carrito);
            });
    }
}
