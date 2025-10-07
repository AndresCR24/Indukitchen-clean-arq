package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.domain.service.ProductoService;
import com.indukitchen.indukitchen.web.controller.ProductoController;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para ProductoController:
 * - AAA (Arrange / Act / Assert)
 * - Mockito para mocks / stubs
 * - ArgumentCaptor para verificar mapping del body
 * - AssertJ para aserciones fluidas
 * - Mantiene tests rápidos y aislados (FIRST)
 */
@WebMvcTest(ProductoController.class)
@Import(TestJacksonConfig.class)
class ProductoControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    ProductoService productoService;

    @MockitoBean
    IndukitchenAiService indukitchenAiService;

    @Test
    @DisplayName("GET /productos -> 200 y arreglo vacío")
    void getAll_returns200AndEmptyArray() throws Exception {
        // Arrange
        given(productoService.getAll()).willReturn(Collections.emptyList());

        // Act & Assert
        mvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        then(productoService).should().getAll();
        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /productos -> 200 y retorna lista de productos")
    void getAll_returns200WithProductList() throws Exception {
        // Arrange
        ProductoDto producto1 = new ProductoDto(1L, "Arroz", "Arroz premium",
                new BigDecimal("5000"), 100, 1.0, "arroz.jpg");
        ProductoDto producto2 = new ProductoDto(2L, "Frijol", "Frijol rojo",
                new BigDecimal("3500"), 50, 0.5, "frijol.jpg");
        List<ProductoDto> productos = Arrays.asList(producto1, producto2);

        given(productoService.getAll()).willReturn(productos);

        // Act & Assert
        mvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Arroz"))
                .andExpect(jsonPath("$[1].nombre").value("Frijol"));

        then(productoService).should().getAll();
        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /productos/{id} -> 200 cuando existe")
    void getById_found_returns200() throws Exception {
        // Arrange
        ProductoDto producto = new ProductoDto(1L, "Arroz", "Arroz premium",
                new BigDecimal("5000"), 100, 1.0, "arroz.jpg");
        given(productoService.getById(1L)).willReturn(producto);

        // Act & Assert
        mvc.perform(get("/productos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Arroz"))
                .andExpect(jsonPath("$.precio").value(5000));

        then(productoService).should().getById(1L);
        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /productos/{id} -> 404 cuando no existe")
    void getById_notFound_returns404() throws Exception {
        // Arrange
        given(productoService.getById(99L)).willReturn(null);

        // Act & Assert
        mvc.perform(get("/productos/99"))
                .andExpect(status().isNotFound());

        then(productoService).should().getById(99L);
        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /productos -> 201 y verifica mapping del body")
    void add_creates_returns201_andVerifiesPayload() throws Exception {
        // Arrange
        Map<String, Object> payload = Map.of(
                "id", 1L,
                "nombre", "Pasta",
                "descripcion", "Pasta italiana",
                "precio", 8000,
                "existencia", 200,
                "peso", 0.5,
                "imagen", "pasta.jpg"
        );

        ProductoDto saved = new ProductoDto(1L, "Pasta", "Pasta italiana",
                new BigDecimal("8000"), 200, 0.5, "pasta.jpg");
        given(productoService.add(any(ProductoDto.class))).willReturn(saved);

        // Act
        mvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Pasta"));

        // Assert: verificamos que se llamó al servicio con el DTO mapeado correctamente
        ArgumentCaptor<ProductoDto> captor = ArgumentCaptor.forClass(ProductoDto.class);
        then(productoService).should().add(captor.capture());

        ProductoDto captured = captor.getValue();
        assertThat(captured).isNotNull();
        assertThat(captured.nombre()).isEqualTo("Pasta");
        assertThat(captured.descripcion()).isEqualTo("Pasta italiana");
        assertThat(captured.existencia()).isEqualTo(200);

        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /productos con campos nulos -> crea producto")
    void add_withNullFields_createsProduct() throws Exception {
        // Arrange: El controlador no valida campos, así que acepta cualquier payload
        ProductoDto saved = new ProductoDto(1L, null, null, null, null, null, null);
        given(productoService.add(any(ProductoDto.class))).willReturn(saved);

        // Act & Assert
        mvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());

        then(productoService).should().add(any(ProductoDto.class));
        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("DELETE /productos/{id} -> 200 y verifica interacción")
    void delete_returns200() throws Exception {
        // Arrange
        willDoNothing().given(productoService).delete(11L);

        // Act & Assert
        mvc.perform(delete("/productos/11"))
                .andExpect(status().isOk());

        then(productoService).should().delete(11L);
        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("DELETE /productos/{id} -> verifica que se pasa el ID correcto")
    void delete_verifiesCorrectIdPassedToService() throws Exception {
        // Arrange
        willDoNothing().given(productoService).delete(anyLong());

        // Act
        mvc.perform(delete("/productos/42"))
                .andExpect(status().isOk());

        // Assert: capturamos el ID pasado al servicio
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        then(productoService).should().delete(captor.capture());

        Long capturedId = captor.getValue();
        assertThat(capturedId).isEqualTo(42L);

        then(productoService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /productos/suggest -> 200 y retorna sugerencia de IA")
    void suggest_returns200AndStringBody() throws Exception {
        // Arrange
        given(indukitchenAiService.generateProductSuggestion(anyString()))
                .willReturn("sugerencia producto");

        // Act & Assert
        mvc.perform(post("/productos/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userPreferences\":\"algo\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("sugerencia producto"));

        then(indukitchenAiService).should().generateProductSuggestion("algo");
        then(indukitchenAiService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /productos/suggest -> verifica que pasa las preferencias correctamente")
    void suggest_verifiesUserPreferencesPassedToService() throws Exception {
        // Arrange
        given(indukitchenAiService.generateProductSuggestion(anyString()))
                .willReturn("sugerencia");

        // Act
        mvc.perform(post("/productos/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userPreferences\":\"productos orgánicos\"}"))
                .andExpect(status().isOk());

        // Assert: capturamos el argumento pasado al servicio
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        then(indukitchenAiService).should().generateProductSuggestion(captor.capture());

        String capturedPreferences = captor.getValue();
        assertThat(capturedPreferences).isEqualTo("productos orgánicos");

        then(indukitchenAiService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /productos/suggest con preferencias vacías -> 200 con sugerencia")
    void suggest_emptyPreferences_returns200() throws Exception {
        // Arrange
        given(indukitchenAiService.generateProductSuggestion(anyString()))
                .willReturn("sugerencia general");

        // Act & Assert
        mvc.perform(post("/productos/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userPreferences\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("sugerencia general"));

        then(indukitchenAiService).should().generateProductSuggestion("");
        then(indukitchenAiService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("POST /productos/suggest -> maneja respuesta larga de IA")
    void suggest_handlesLongAiResponse() throws Exception {
        // Arrange
        String longSuggestion = "Esta es una sugerencia muy detallada sobre productos que incluye "
                + "múltiples recomendaciones basadas en las preferencias del usuario y el análisis "
                + "de tendencias del mercado actual de alimentos y productos para cocina industrial.";
        given(indukitchenAiService.generateProductSuggestion(anyString()))
                .willReturn(longSuggestion);

        // Act & Assert
        mvc.perform(post("/productos/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userPreferences\":\"productos premium\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(longSuggestion));

        then(indukitchenAiService).should().generateProductSuggestion("productos premium");
        then(indukitchenAiService).shouldHaveNoMoreInteractions();
    }
}

