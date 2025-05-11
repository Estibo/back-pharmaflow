package com.pharmaflow.BackPharmaFlow.controller;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.pharmaflow.BackPharmaFlow.model.entity.Inventario;
import com.pharmaflow.BackPharmaFlow.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private InventarioRepository inventarioRepository;

    @PostMapping
    public ResponseEntity<?> realizarVenta(@RequestBody VentaRequest ventaRequest) {
        BigDecimal total = BigDecimal.ZERO;

        for (VentaProducto producto : ventaRequest.getProductos()) {
            Inventario inventario = inventarioRepository.findById(producto.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (inventario.getCantidad() < producto.getCantidad()) {
                return ResponseEntity.badRequest().body("Stock insuficiente para el producto: " + inventario.getNombreProducto());
            }

            inventario.setCantidad(inventario.getCantidad() - producto.getCantidad());
            inventarioRepository.save(inventario);

            total = total.add(inventario.getPrecioUnitario().multiply(BigDecimal.valueOf(producto.getCantidad())));
        }

        ByteArrayOutputStream pdfStream = generarFacturaPDF(ventaRequest, total);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=factura.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfStream.toByteArray());
    }

    private ByteArrayOutputStream generarFacturaPDF(VentaRequest ventaRequest, BigDecimal total) {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(pdfStream);
        com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Factura de Venta"));
        document.add(new Paragraph("Cliente: " + ventaRequest.getCliente()));
        document.add(new Paragraph("Medio de Pago: " + ventaRequest.getMedioPago()));
        document.add(new Paragraph("Productos:"));

        for (VentaProducto producto : ventaRequest.getProductos()) {
            document.add(new Paragraph("- Producto ID: " + producto.getId() +
                    ", Cantidad: " + producto.getCantidad()));
        }

        document.add(new Paragraph("Total: $" + total));
        document.close();

        return pdfStream;
    }

    static class VentaRequest {
        private String cliente;
        private String medioPago;
        private List<VentaProducto> productos;

        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public String getMedioPago() {
            return medioPago;
        }

        public void setMedioPago(String medioPago) {
            this.medioPago = medioPago;
        }

        public List<VentaProducto> getProductos() {
            return productos;
        }

        public void setProductos(List<VentaProducto> productos) {
            this.productos = productos;
        }
    }

    static class VentaProducto {
        private Integer id;
        private Integer cantidad;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
