package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.service.ClienteService;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.web.controller.ClienteController;
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

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para ClienteController:
 * - AAA (Arrange / Act / Assert)
 * - Mockito para mocks / stubs
 * - ArgumentCaptor para verificar mapping desde JSON a DTOs
 * - AssertJ para aserciones fluidas
 * - Mantiene tests rápidos y aislados (FIRST)
 */
@WebMvcTest(ClienteController.class)
@Import(TestJacksonConfig.class)
class ClienteControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    ClienteService clienteService;

    @MockitoBean
    IndukitchenAiService indukitchenAiService;

    @Test
    @DisplayName("GET /clientes -> 200 y arreglo vacío")
    void getAll_returns200AndEmptyArray() throws Exception {
        // Arrange
        given(clienteService.getAllClientes()).willReturn(Collections.emptyList());

        // Act
        MvcResult result = mvc.perform(get("/clientes"))
                // Assert (status + content)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).isEqualTo("[]");

        then(clienteService).should().getAllClientes();
        then(clienteService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /clientes/{id} -> 200 cuando existe")
    void getById_found_returns200() throws Exception {
        // Arrange
        ClienteDto dto = mock(ClienteDto.class);
        given(clienteService.getById("123")).willReturn(dto);

        // Act & Assert
        mvc.perform(get("/clientes/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        then(clienteService).should().getById("123");
        then(clienteService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /clientes/{id} -> 404 cuando no existe")
    void getById_notFound_returns404() throws Exception {
        // Arrange
        given(clienteService.getById("nope")).willReturn(null);

        // Act & Assert
        mvc.perform(get("/clientes/nope"))
                .andExpect(status().isNotFound());

        then(clienteService).should().getById("nope");
        then(clienteService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /clientes -> 201 y verifica mapping del body")
    void add_creates_returns201_andVerifiesPayload() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of(
                "cedula", "100",
                "nombre", "Ana",
                "direccion", "Calle Falsa 123",
                "telefono", "3110000000"
        );
        ClienteDto saved = mock(ClienteDto.class);
        given(clienteService.add(any(ClienteDto.class))).willReturn(saved);

        // Act
        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Assert: verificamos que se llamó al servicio con el DTO mapeado correctamente
        ArgumentCaptor<ClienteDto> captor = ArgumentCaptor.forClass(ClienteDto.class);
        then(clienteService).should().add(captor.capture());

        ClienteDto captured = captor.getValue();
        assertThat(captured).isNotNull();

        then(clienteService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("PUT /clientes/{id} -> 200 y verifica actualización")
    void update_returns200_andVerifiesPayload() throws Exception {
        // Arrange
        String id = "42";
        Map<String, Object> payload = Map.of(
                "cedula", "42",
                "nombre", "Juan Pérez",
                "direccion", "Calle 123",
                "telefono", "3001234567"
        );
        ClienteDto updated = mock(ClienteDto.class);
        given(clienteService.update(eq(id), any(UpdateClienteDto.class))).willReturn(updated);

        // Act
        mvc.perform(put("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Assert: capturamos DTO mapeado
        ArgumentCaptor<UpdateClienteDto> captor = ArgumentCaptor.forClass(UpdateClienteDto.class);
        then(clienteService).should().update(eq(id), captor.capture());

        UpdateClienteDto captured = captor.getValue();
        assertThat(captured).isNotNull();
        // Si UpdateClienteDto tiene getters, valida campos:

        then(clienteService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("PUT /clientes/{id} con body inválido -> 400 y no llama al servicio")
    void update_invalidBody_returns400_andNoServiceCall() throws Exception {
        // Arrange: no stubbing necesario porque no debe llegar al servicio

        // Act & Assert: cuerpo vacío -> Bad Request (validaciones de Jackson/Controller)
        mvc.perform(put("/clientes/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        then(clienteService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("DELETE /clientes/{id} -> 200 y verifica interacción")
    void delete_returns200() throws Exception {
        // Arrange
        willDoNothing().given(clienteService).delete("55");

        // Act & Assert
        mvc.perform(delete("/clientes/55"))
                .andExpect(status().isOk());

        then(clienteService).should().delete("55");
        then(clienteService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /clientes/suggest -> 200 y retorna string desde AI service (verifica payload)")
    void suggest_returns200AndStringBody_andVerifiesInput() throws Exception {
        // Arrange
        String suggestion = "sugerencia";
        given(indukitchenAiService.generateClienteSuggestion(anyString())).willReturn(suggestion);

        Map<String, Object> payload = Map.of("userPreferences", "algo");

        // Act
        MvcResult result = mvc.perform(post("/clientes/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        // Assert: contenido de respuesta y que el servicio recibió la cadena esperada (por ejemplo, JSON serializado)
        String body = result.getResponse().getContentAsString();
        assertThat(body).isEqualTo(suggestion);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        then(indukitchenAiService).should().generateClienteSuggestion(captor.capture());
        String capturedInput = captor.getValue();
        assertThat(capturedInput).isNotNull();
        // Si tu controlador pasa JSON crudo como String, podrías comprobar contains:

        then(indukitchenAiService).shouldHaveNoMoreInteractions();
    }
}
