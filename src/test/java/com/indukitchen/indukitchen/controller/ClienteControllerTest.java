package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.service.ClienteService;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.web.controller.ClienteController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import(TestJacksonConfig.class)
class ClienteControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    ClienteService clienteService;
    @MockitoBean
    IndukitchenAiService indukitchenAiService;

    @Test
    void getAll_returns200AndEmptyArray() throws Exception {
        given(clienteService.getAllClientes()).willReturn(Collections.emptyList());

        mvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getById_found_returns200() throws Exception {
        given(clienteService.getById("123")).willReturn(mock(ClienteDto.class));

        mvc.perform(get("/clientes/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        given(clienteService.getById("nope")).willReturn(null);

        mvc.perform(get("/clientes/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void add_creates_returns201() throws Exception {
        given(clienteService.add(any(ClienteDto.class))).willReturn(mock(ClienteDto.class));

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void update_returns200() throws Exception {
        given(clienteService.update(eq("42"), any(UpdateClienteDto.class)))
                .willReturn(mock(ClienteDto.class));

        mvc.perform(put("/clientes/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "cedula": "42",
                  "nombre": "Juan Pérez",
                  "direccion": "Calle 123",
                  "telefono": "3001234567"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void update_invalidBody_returns400() throws Exception {
        mvc.perform(put("/clientes/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }



    @Test
    void delete_returns200() throws Exception {
        willDoNothing().given(clienteService).delete("55");

        mvc.perform(delete("/clientes/55"))
                .andExpect(status().isOk());

        then(clienteService).should().delete("55");
    }

    @Test
    void suggest_returns200AndStringBody() throws Exception {
        given(indukitchenAiService.generateClienteSuggestion(anyString()))
                .willReturn("sugerencia");

        // El controller espera SuggestClienteDto con userPreferences(), pero para el test
        // basta con mandar un JSON con la propiedad; Jackson lo mapeará si existe en tu DTO.
        mvc.perform(post("/clientes/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userPreferences\":\"algo\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("sugerencia"));
    }
}
