package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.web.controller.HelloController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para HelloController:
 * - AAA (Arrange / Act / Assert)
 * - Mockito para mocks / stubs
 * - AssertJ para aserciones fluidas
 * - Mantiene tests rápidos y aislados (FIRST)
 */
@WebMvcTest(HelloController.class)
@Import(TestJacksonConfig.class)
@TestPropertySource(properties = "spring.application.name=InduKitchenApp")
class HelloControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    IndukitchenAiService aiService;

    @Test
    @DisplayName("GET /hello -> 200 y retorna saludo del servicio de IA")
    void hello_returnsGreetingFromService() throws Exception {
        // Arrange
        given(aiService.generateGreeting(anyString())).willReturn("¡Hola desde InduKitchenApp!");

        // Act & Assert
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("¡Hola desde InduKitchenApp!"));

        // Assert: verificamos que el servicio fue llamado
        then(aiService).should().generateGreeting("InduKitchenApp");
        then(aiService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /hello -> verifica que pasa el nombre de la aplicación al servicio")
    void hello_passesApplicationNameToAiService() throws Exception {
        // Arrange
        given(aiService.generateGreeting(anyString())).willReturn("Greeting");

        // Act
        mvc.perform(get("/hello"))
                .andExpect(status().isOk());

        // Assert: capturamos el argumento pasado al servicio
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        then(aiService).should().generateGreeting(captor.capture());

        String capturedName = captor.getValue();
        assertThat(capturedName).isEqualTo("InduKitchenApp");

        then(aiService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /hello -> retorna texto plano")
    void hello_returnsPlainText() throws Exception {
        // Arrange
        given(aiService.generateGreeting(anyString())).willReturn("Test greeting");

        // Act & Assert
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        then(aiService).should().generateGreeting("InduKitchenApp");
    }

    @Test
    @DisplayName("GET /hello -> maneja saludo vacío del servicio")
    void hello_handlesEmptyGreeting() throws Exception {
        // Arrange
        given(aiService.generateGreeting(anyString())).willReturn("");

        // Act & Assert
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        then(aiService).should().generateGreeting("InduKitchenApp");
        then(aiService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /hello -> maneja saludo largo del servicio")
    void hello_handlesLongGreeting() throws Exception {
        // Arrange
        String longGreeting = "Este es un saludo muy largo generado por el servicio de IA que contiene mucha información";
        given(aiService.generateGreeting(anyString())).willReturn(longGreeting);

        // Act & Assert
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(longGreeting));

        then(aiService).should().generateGreeting("InduKitchenApp");
        then(aiService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("GET /hello -> maneja saludo con caracteres especiales")
    void hello_handlesSpecialCharactersInGreeting() throws Exception {
        // Arrange
        String specialGreeting = "¡Hola! 你好 مرحبا Привет 🎉";
        given(aiService.generateGreeting(anyString())).willReturn(specialGreeting);

        // Act & Assert
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(specialGreeting));

        then(aiService).should().generateGreeting("InduKitchenApp");
        then(aiService).shouldHaveNoMoreInteractions();
    }
}
