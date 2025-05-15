package com.pharmaflow.BackPharmaFlow.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.pharmaflow.BackPharmaFlow.model.entity.*;
import com.pharmaflow.BackPharmaFlow.repository.*;
import com.pharmaflow.BackPharmaFlow.exceptions.VentaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarVenta(@RequestBody VentaRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario empleado = usuarioRepository.findByCorreo(auth.getName())
                    .orElseThrow(VentaException::usuarioNoEncontrado);

            Usuario cliente = usuarioRepository.findById(request.getCedulaCliente())
                    .orElseThrow(VentaException::usuarioNoEncontrado);

            Carrito carrito = carritoRepository.findByUsuarioCedulaAndEstadoTrue(cliente.getCedula())
                    .orElseThrow(VentaException::carritoNoEncontrado);

            if (carrito.getItems().isEmpty()) {
                throw VentaException.carritoVacio();
            }

            // Verificar stock antes de procesar
            for (ItemCarrito item : carrito.getItems()) {
                Inventario producto = item.getProducto();
                if (producto.getCantidad() < item.getCantidad()) {
                    throw VentaException.stockInsuficiente(producto.getNombreProducto());
                }
            }

            // Crear la venta
            Venta venta = crearVenta(cliente, empleado, request.getMetodoPago());
            
            // Procesar el carrito
            BigDecimal total = procesarCarrito(venta, carrito);
            venta.setTotal(total);
            ventaRepository.save(venta);

            // Cerrar el carrito
            carrito.setEstado(false);
            carritoRepository.save(carrito);

            // Generar y retornar factura PDF
            byte[] pdf = generarFacturaPDF(venta);
            return ResponseEntity.ok()
                .headers(generarHeadersFactura(venta.getNumeroFactura()))
                .body(pdf);

        } catch (VentaException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al procesar venta", e);
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("ERROR_INTERNO", "Error interno al procesar la venta"));
        }
    }

    private Venta crearVenta(Usuario cliente, Usuario empleado, String metodoPago) {
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);
        venta.setMetodoPago(metodoPago);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setDetalles(new ArrayList<>());
        return venta;
    }

    private BigDecimal procesarCarrito(Venta venta, Carrito carrito) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : carrito.getItems()) {
            Inventario producto = item.getProducto();
            
            // Actualizar inventario
            producto.setCantidad(producto.getCantidad() - item.getCantidad());
            inventarioRepository.save(producto);

            // Crear detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            
            BigDecimal subtotal = item.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            venta.getDetalles().add(detalle);
        }

        return total;
    }

    private HttpHeaders generarHeadersFactura(String numeroFactura) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "factura-" + numeroFactura + ".pdf");
        return headers;
    }

    private byte[] generarFacturaPDF(Venta venta) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado
            document.add(new Paragraph("PHARMAFLOW")
                .setFontSize(24)
                .setBold());
            
            document.add(new Paragraph("Factura #: " + venta.getNumeroFactura()));
            document.add(new Paragraph("Fecha: " + 
                venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

            // Datos del cliente
            document.add(new Paragraph("\nDatos del Cliente:"));
            document.add(new Paragraph("Nombre: " + venta.getCliente().getNombre() + 
                " " + venta.getCliente().getApellido()));
            document.add(new Paragraph("Cédula: " + venta.getCliente().getCedula()));
            document.add(new Paragraph("Correo: " + venta.getCliente().getCorreo()));

            // Datos del empleado
            document.add(new Paragraph("\nAtendido por:"));
            document.add(new Paragraph("Nombre: " + venta.getEmpleado().getNombre() + 
                " " + venta.getEmpleado().getApellido()));
            document.add(new Paragraph("Correo: " + venta.getEmpleado().getCorreo()));

            // Tabla de productos
            Table table = new Table(4);
            table.addCell("Producto");
            table.addCell("Cantidad");
            table.addCell("Precio Unitario");
            table.addCell("Subtotal");

            for (DetalleVenta detalle : venta.getDetalles()) {
                table.addCell(detalle.getProducto().getNombreProducto());
                table.addCell(detalle.getCantidad().toString());
                table.addCell("$" + detalle.getPrecioUnitario().toString());
                BigDecimal subtotal = detalle.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
                table.addCell("$" + subtotal.toString());
            }

            document.add(table);

            // Total y método de pago
            document.add(new Paragraph("\nTotal: $" + venta.getTotal().toString())
                .setBold());
            document.add(new Paragraph("Método de pago: " + venta.getMetodoPago()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    @Getter @Setter
    static class ErrorResponse {
        private String code;
        private String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    static class VentaRequest {
        private String cedulaCliente;
        private String metodoPago;

        public String getCedulaCliente() {
            return cedulaCliente;
        }

        public void setCedulaCliente(String cedulaCliente) {
            this.cedulaCliente = cedulaCliente;
        }

        public String getMetodoPago() {
            return metodoPago;
        }

        public void setMetodoPago(String metodoPago) {
            this.metodoPago = metodoPago;
        }
    }
}
