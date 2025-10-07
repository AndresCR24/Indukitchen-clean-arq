package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.service.FacturaService;
import com.indukitchen.indukitchen.web.controller.FacturaController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para FacturaController:
 * - AAA (Arrange / Act / Assert)
 * - Mockito para mocks / stubs
 * - ArgumentCaptor para verificar mapping de request bodies
 * - AssertJ para aserciones fluidas
 * - Mantiene tests rápidos y aislados (FIRST)
 */
@WebMvcTest(FacturaController.class)
@Import(TestJacksonConfig.class)
class FacturaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    FacturaService facturaService;

    @Test
    @DisplayName("GET /facturas -> 200 y arreglo vacío")
    void getAll_returns200AndEmptyArray() throws Exception {
        // Arrange
        given(facturaService.getAllFacturas()).willReturn(Collections.emptyList());

        // Act
        MvcResult result = mvc.perform(get("/facturas"))
                // Assert status + content-type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert body + service interaction
        String body = result.getResponse().getContentAsString();
        assertThat(body).isEqualTo("[]");

        then(facturaService).should().getAllFacturas();
        then(facturaService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /facturas/{id} -> 200 cuando existe")
    void getById_found_returns200() throws Exception {
        // Arrange
        FacturaDto dto = mock(FacturaDto.class);
        given(facturaService.getById(1L)).willReturn(dto);

        // Act & Assert
        mvc.perform(get("/facturas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        then(facturaService).should().getById(1L);
        then(facturaService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /facturas/{id} -> 404 cuando no existe")
    void getById_notFound_returns404() throws Exception {
        // Arrange
        given(facturaService.getById(999L)).willReturn(null);

        // Act & Assert
        mvc.perform(get("/facturas/999"))
                .andExpect(status().isNotFound());

        then(facturaService).should().getById(999L);
        then(facturaService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /facturas -> 201 y verifica mapping del body")
    void add_creates_returns201_andVerifiesPayload() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of(
                "id", 1L,
                "idCarrito", 500L,
                "idMetodoPago", 1
        );

        FacturaDto saved = mock(FacturaDto.class);
        given(facturaService.add(any(FacturaDto.class))).willReturn(saved);

        // Act
        mvc.perform(post("/facturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Assert: verificamos mapping hacia el servicio
        ArgumentCaptor<FacturaDto> captor = ArgumentCaptor.forClass(FacturaDto.class);
        then(facturaService).should().add(captor.capture());

        FacturaDto captured = captor.getValue();
        assertThat(captured).isNotNull();
        // Verificar que los campos se mapearon correctamente
        assertThat(captured.id()).isEqualTo(1L);
        assertThat(captured.idCarrito()).isEqualTo(500L);
        assertThat(captured.idMetodoPago()).isEqualTo(1);

        then(facturaService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("DELETE /facturas/{id} -> 200 y verifica interacción")
    void delete_returns200() throws Exception {
        // Arrange
        willDoNothing().given(facturaService).delete(5L);

        // Act & Assert
        mvc.perform(delete("/facturas/5"))
                .andExpect(status().isOk());

        then(facturaService).should().delete(5L);
        then(facturaService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /facturas/{id}/pdf -> 200 y retorna PDF inline cuando existe")
    void getFacturaPdf_returns200PdfInline() throws Exception {
        // Arrange: creamos un ByteArrayOutputStream con contenido "PDF"
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("PDF".getBytes(StandardCharsets.UTF_8));
        given(facturaService.generateFacturaPdf(7L)).willReturn(baos);

        // Act & Assert: content-type PDF, disposition inline y bytes iguales
        mvc.perform(get("/facturas/7/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", containsString("inline")))
                .andExpect(content().bytes(baos.toByteArray()));

        then(facturaService).should().generateFacturaPdf(7L);
        then(facturaService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /facturas/{id}/pdf -> 404 cuando el PDF no está disponible")
    void getFacturaPdf_notFound_returns404() throws Exception {
        // Arrange: servicio retorna stream vacío para indicar no encontrado
        ByteArrayOutputStream empty = new ByteArrayOutputStream(); // length 0
        given(facturaService.generateFacturaPdf(8L)).willReturn(empty);

        // Act & Assert
        mvc.perform(get("/facturas/8/pdf"))
                .andExpect(status().isNotFound());

        then(facturaService).should().generateFacturaPdf(8L);
        then(facturaService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /facturas/{id}/email -> 202 y delega en el servicio")
    void sendFacturaEmail_returns202_andDelegatesToService() throws Exception {
        // Arrange
        willDoNothing().given(facturaService).generarEnviarFactura(10L);

        // Act & Assert
        mvc.perform(post("/facturas/10/email"))
                .andExpect(status().isAccepted());

        then(facturaService).should().generarEnviarFactura(10L);
        then(facturaService).shouldHaveNoMoreInteractions();
    }
}

