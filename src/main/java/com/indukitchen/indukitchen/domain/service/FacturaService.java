package com.indukitchen.indukitchen.domain.service;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.repository.FacturaRepository;
import com.indukitchen.indukitchen.persistence.crud.CrudFacturaEntity;
import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class FacturaService {

    // ----- Repos / Dependencias -----
    private final FacturaRepository facturaRepository; // tu repo de dominio (DTOs)
    private final CrudFacturaEntity crudFactura;       // JPA para cargar entidad + relaciones
    private final JavaMailSender mailSender;           // para enviar email

    // Formato numérico para PDF
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public FacturaService(FacturaRepository facturaRepository, CrudFacturaEntity crudFactura, JavaMailSender mailSender) {
        this.facturaRepository = facturaRepository;
        this.crudFactura = crudFactura;
        this.mailSender = mailSender;
    }


    public List<FacturaDto> getAllFacturas() {
        return this.facturaRepository.getAll();
    }

    public FacturaDto getById(long id) {
        return this.facturaRepository.getById(id);
    }

    public FacturaDto add(FacturaDto facturaDto) {
        return this.facturaRepository.save(facturaDto);
    }

    public void delete(long id) {
        this.facturaRepository.delete(id);
    }

    // ===================== NUEVO: Cálculo de total =====================

    /**
     * Calcula el total de la factura asumiendo ManyToMany simple:
     * cada producto del carrito cuenta cantidad = 1.
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotal(long facturaId) {
        FacturaEntity f = crudFactura.findById(facturaId).orElse(null);
        if (f == null || f.getCarritoFactura() == null) return BigDecimal.ZERO;

        List<ProductoEntity> productos = f.getCarritoFactura().getProductos();
        if (productos == null || productos.isEmpty()) return BigDecimal.ZERO;

        return productos.stream()
                .map(p -> p.getPrecio() != null ? p.getPrecio() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ===================== NUEVO: Generación de PDF =====================

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateFacturaPdf(long facturaId) {
        FacturaEntity factura = crudFactura.findById(facturaId).orElse(null);
        if (factura == null) return new ByteArrayOutputStream();
        return generateFacturaPdf(factura);
    }

    /**
     * Genera un PDF con logo, cabecera y tabla de productos (cantidad=1).
     * Coloca un archivo "logo.png" en src/main/resources/ si quieres que aparezca.
     */
    public ByteArrayOutputStream generateFacturaPdf(FacturaEntity factura) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Logo (opcional)
            try (InputStream imageStream = getClass().getClassLoader().getResourceAsStream("logo.png")) {
                if (imageStream != null) {
                    ImageData imageData = ImageDataFactory.create(imageStream.readAllBytes());
                    document.add(new Image(imageData).setHorizontalAlignment(HorizontalAlignment.CENTER));
                }
            }

            // Encabezado
            document.add(new Paragraph("Factura")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());
            document.add(new Paragraph("Factura ID: " + factura.getId()));

            if (factura.getCarritoFactura() != null && factura.getCarritoFactura().getCliente() != null) {
                var cli = factura.getCarritoFactura().getCliente();
                document.add(new Paragraph("Cliente: " + safe(cli.getNombre())));
                document.add(new Paragraph("Correo: " + safe(cli.getCorreo())));
            }
            document.add(new Paragraph(" ")); // espacio

            // Tabla
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Cantidad").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio Total").setBold()));

            BigDecimal total = BigDecimal.ZERO;

            if (factura.getCarritoFactura() != null) {
                List<ProductoEntity> productos = factura.getCarritoFactura().getProductos();
                if (productos != null) {
                    for (ProductoEntity p : productos) {
                        String nombre = safe(p.getNombre());
                        BigDecimal precioUnit = p.getPrecio() != null ? p.getPrecio() : BigDecimal.ZERO;
                        int cantidad = 1; // ManyToMany simple
                        BigDecimal precioTot = precioUnit.multiply(BigDecimal.valueOf(cantidad));
                        total = total.add(precioTot);

                        table.addCell(new Cell().add(new Paragraph(nombre)));
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(cantidad))));
                        table.addCell(new Cell().add(new Paragraph(decimalFormat.format(precioUnit))));
                        table.addCell(new Cell().add(new Paragraph(decimalFormat.format(precioTot))));
                    }
                }
            }

            document.add(table);

            // IVA y total
            BigDecimal iva = total.multiply(BigDecimal.valueOf(0.19));
            BigDecimal totalConIva = total.add(iva);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total: " + decimalFormat.format(total))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(14).setBold());
            document.add(new Paragraph("IVA (19%): " + decimalFormat.format(iva))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(14).setBold());
            document.add(new Paragraph("Total + IVA: " + decimalFormat.format(totalConIva))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(14).setBold());

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    // ===================== NUEVO: Email con adjunto =====================

    public void sendEmailWithAttachment(String to,
                                        String subject,
                                        String text,
                                        byte[] pdfBytes,
                                        String pdfFilename) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("trabajo.indukitchen3@hotmail.com"); // ajusta remitente real
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(pdfFilename, new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

    /**
     * Helper opcional: genera el PDF de la factura y lo envía por email.
     */
    @Transactional(readOnly = true)
    public void emailFacturaPdf(long facturaId, String to, String subject, String text) throws MessagingException {
        ByteArrayOutputStream pdf = generateFacturaPdf(facturaId);
        sendEmailWithAttachment(to, subject, text, pdf.toByteArray(), "factura-" + facturaId + ".pdf");
    }

    @Transactional(readOnly = true)
    public void generarEnviarFactura(long facturaId) {
        var factura = crudFactura.findById(facturaId).orElse(null);
        if (factura == null
                || factura.getCarritoFactura() == null
                || factura.getCarritoFactura().getCliente() == null) {
            return;
        }
        generarEnviarFactura(factura); // versión que recibe FacturaEntity
    }

    public void generarEnviarFactura(FacturaEntity factura) {
        try {
            // Generar PDF desde la ENTIDAD (tienes relaciones disponibles)
            ByteArrayOutputStream pdfOutputStream = generateFacturaPdf(factura);
            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Email del cliente desde la ENTIDAD
            String emailCliente = factura.getCarritoFactura().getCliente().getCorreo();
            if (emailCliente == null || emailCliente.isBlank()) return;

            sendEmailWithAttachment(
                    emailCliente,
                    "Tu factura de InduKitchen",
                    "Adjunto encontrarás tu factura.",
                    pdfBytes,
                    "factura-" + factura.getId() + ".pdf"
            );
        } catch (Exception e) {
            e.printStackTrace(); // o logger
        }
    }

    // ===================== Utilidades =====================

    private static String safe(String s) { return s == null ? "" : s; }
}
