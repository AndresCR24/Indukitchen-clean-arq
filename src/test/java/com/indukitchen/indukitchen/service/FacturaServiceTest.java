package com.indukitchen.indukitchen.service;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.repository.FacturaRepository;
import com.indukitchen.indukitchen.domain.service.FacturaService;
import com.indukitchen.indukitchen.persistence.crud.CrudFacturaEntity;
import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    FacturaRepository facturaRepository;
    @Mock
    CrudFacturaEntity crudFactura;
    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    FacturaService facturaService;

    // ---------------- Delegaciones simples ----------------

    @Test
    void getAllFacturas_delegatesToRepository() {
        var list = List.of(new FacturaDto(1L, 2L, 3));
        given(facturaRepository.getAll()).willReturn(list);

        var res = facturaService.getAllFacturas();

        assertSame(list, res);
        then(facturaRepository).should().getAll();
    }

    @Test
    void getById_delegatesToRepository() {
        var dto = new FacturaDto(9L, 8L, 7);
        given(facturaRepository.getById(9L)).willReturn(dto);

        var res = facturaService.getById(9L);

        assertSame(dto, res);
        then(facturaRepository).should().getById(9L);
    }

    @Test
    void add_delegatesToRepository() {
        var input = new FacturaDto(0L, 33L, 1);
        var saved = new FacturaDto(100L, 33L, 1);
        given(facturaRepository.save(input)).willReturn(saved);

        var res = facturaService.add(input);

        assertSame(saved, res);
        then(facturaRepository).should().save(input);
    }

    @Test
    void delete_delegatesToRepository() {
        willDoNothing().given(facturaRepository).delete(7L);

        facturaService.delete(7L);

        then(facturaRepository).should().delete(7L);
    }

    // ---------------- calculateTotal ----------------

    @Test
    void calculateTotal_sumsProductPrices() {
        // FacturaEntity con deep stubs para no crear CarritoEntity explícito
        var factura = mock(FacturaEntity.class, RETURNS_DEEP_STUBS);

        // Productos (algunos con null precio)
        var p1 = mock(ProductoEntity.class); given(p1.getPrecio()).willReturn(new BigDecimal("10.50"));
        var p2 = mock(ProductoEntity.class); given(p2.getPrecio()).willReturn(null); // cuenta como 0
        var p3 = mock(ProductoEntity.class); given(p3.getPrecio()).willReturn(new BigDecimal("2.25"));

        given(factura.getCarritoFactura().getProductos()).willReturn(List.of(p1, p2, p3));
        given(crudFactura.findById(1L)).willReturn(Optional.of(factura));

        var total = facturaService.calculateTotal(1L);

        assertEquals(new BigDecimal("12.75"), total);
    }

    @Test
    void calculateTotal_returnsZero_whenNoFacturaOrNoProductos() {
        // Caso 1: no existe
        given(crudFactura.findById(1L)).willReturn(Optional.empty());
        assertEquals(BigDecimal.ZERO, facturaService.calculateTotal(1L));

        // Caso 2: existe pero sin carrito
        var f2 = mock(FacturaEntity.class); given(f2.getCarritoFactura()).willReturn(null);
        given(crudFactura.findById(2L)).willReturn(Optional.of(f2));
        assertEquals(BigDecimal.ZERO, facturaService.calculateTotal(2L));

        // Caso 3: carrito sin productos
        var f3 = mock(FacturaEntity.class, RETURNS_DEEP_STUBS);
        given(f3.getCarritoFactura().getProductos()).willReturn(List.of());
        given(crudFactura.findById(3L)).willReturn(Optional.of(f3));
        assertEquals(BigDecimal.ZERO, facturaService.calculateTotal(3L));
    }

    // ---------------- generateFacturaPdf ----------------

    @Test
    void generateFacturaPdf_byId_returnsEmpty_whenNotFound() {
        given(crudFactura.findById(77L)).willReturn(Optional.empty());

        var baos = facturaService.generateFacturaPdf(77L);

        assertNotNull(baos);
        assertEquals(0, baos.size()); // servicio retorna BAOS vacío
    }

    @Test
    void generateFacturaPdf_withEntity_producesBytes() {
        // Factura con productos y cliente (deep stubs)
        var factura = mock(FacturaEntity.class, RETURNS_DEEP_STUBS);

        var p1 = mock(ProductoEntity.class);
        given(p1.getNombre()).willReturn("Cuchillo");
        given(p1.getPrecio()).willReturn(new BigDecimal("12.00"));

        var p2 = mock(ProductoEntity.class);
        given(p2.getNombre()).willReturn("Tabla");
        given(p2.getPrecio()).willReturn(new BigDecimal("8.50"));

        given(factura.getCarritoFactura().getProductos()).willReturn(List.of(p1, p2));
        given(factura.getCarritoFactura().getCliente().getNombre()).willReturn("Ana");
        given(factura.getCarritoFactura().getCliente().getCorreo()).willReturn("ana@dominio.com");
        given(factura.getId()).willReturn(555L);

        var pdf = facturaService.generateFacturaPdf(factura);

        assertNotNull(pdf);
        assertTrue(pdf.size() > 0, "El PDF debería tener contenido");
    }

    // ---------------- sendEmailWithAttachment ----------------

    @Test
    void sendEmailWithAttachment_createsMime_andCallsMailSender() throws Exception {
        // Usamos un JavaMailSender real SOLO para crear el MimeMessage (no para enviar)
        JavaMailSenderImpl real = new JavaMailSenderImpl();
        MimeMessage message = real.createMimeMessage();

        given(mailSender.createMimeMessage()).willReturn(message);

        byte[] bytes = "pdf".getBytes();
        facturaService.sendEmailWithAttachment(
                "to@dominio.com", "asunto", "texto", bytes, "archivo.pdf");

        // Verificamos que el mock envió ese mismo mensaje
        then(mailSender).should().send(message);

        // Chequeos básicos del contenido del mensaje
        assertEquals("asunto", message.getSubject());
        assertEquals("to@dominio.com", message.getAllRecipients()[0].toString());
    }

    // ---------------- emailFacturaPdf ----------------

    @Test
    void emailFacturaPdf_usesGeneratedPdfAndSends() throws Exception {
        // Espiamos el servicio para stubear generateFacturaPdf(...)
        FacturaService spyService = Mockito.spy(new FacturaService(facturaRepository, crudFactura, mailSender));

        // Stub del PDF generado
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("PDF".getBytes());
        doReturn(baos).when(spyService).generateFacturaPdf(10L);

        // Preparamos MimeMessage real para no chocar con MimeMessageHelper
        JavaMailSenderImpl real = new JavaMailSenderImpl();
        MimeMessage msg = real.createMimeMessage();
        given(mailSender.createMimeMessage()).willReturn(msg);

        spyService.emailFacturaPdf(10L, "dest@dominio.com", "subj", "txt");

        // Verifica que se envió el mensaje
        then(mailSender).should().send(msg);
        assertEquals("subj", msg.getSubject());
    }

    // ---------------- generarEnviarFactura ----------------

    @Test
    void generarEnviarFactura_byId_sendsWhenClienteHasEmail() {
        // Espiamos para interceptar el envío/creación de PDF
        FacturaService spyService = Mockito.spy(new FacturaService(facturaRepository, crudFactura, mailSender));

        // FacturaEntity con deep stubs: id, correo de cliente
        var factura = mock(FacturaEntity.class, RETURNS_DEEP_STUBS);
        given(factura.getId()).willReturn(999L);
        given(factura.getCarritoFactura().getCliente().getCorreo()).willReturn("cli@dominio.com");

        // findById retorna esa entidad
        given(crudFactura.findById(999L)).willReturn(Optional.of(factura));

        // Stub de generación de PDF (no nos importa su contenido en esta prueba)
        doReturn(new ByteArrayOutputStream()).when(spyService).generateFacturaPdf(factura);

        // Stub del envío (evitamos usar JavaMail real)
        try {
            doNothing().when(spyService).sendEmailWithAttachment(
                    anyString(), anyString(), anyString(), any(byte[].class), anyString());
        } catch (MessagingException e) {
            fail(e);
        }

        spyService.generarEnviarFactura(999L);

        // Verifica que se invocó el envío con el correo del cliente
        try {
            Mockito.verify(spyService).sendEmailWithAttachment(
                    eq("cli@dominio.com"),
                    anyString(),
                    anyString(),
                    any(byte[].class),
                    argThat(name -> name.contains("999"))
            );
        } catch (MessagingException e) {
            fail(e);
        }
    }

}

