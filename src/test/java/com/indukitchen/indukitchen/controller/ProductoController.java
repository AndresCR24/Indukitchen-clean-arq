package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.domain.service.ProductoService;
import com.indukitchen.indukitchen.web.controller.ProductoController;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@Import(TestJacksonConfig.class)
class ProductoControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean
    ProductoService productoService;
    @MockitoBean
    IndukitchenAiService indukitchenAiService;

    @Test
    void getAll_returns200AndEmptyArray() throws Exception {
        given(productoService.getAll()).willReturn(Collections.emptyList());

        mvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getById_found_returns200() throws Exception {
        given(productoService.getById(1L)).willReturn(mock(ProductoDto.class));

        mvc.perform(get("/productos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        given(productoService.getById(99L)).willReturn(null);

        mvc.perform(get("/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void add_creates_returns201() throws Exception {
        given(productoService.add(any(ProductoDto.class))).willReturn(mock(ProductoDto.class));

        mvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void delete_returns200() throws Exception {
        willDoNothing().given(productoService).delete(11L);

        mvc.perform(delete("/productos/11"))
                .andExpect(status().isOk());

        then(productoService).should().delete(11L);
    }

    @Test
    void suggest_returns200AndStringBody() throws Exception {
        given(indukitchenAiService.generateProductSuggestion(anyString()))
                .willReturn("sugerencia producto");

        mvc.perform(post("/productos/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userPreferences\":\"algo\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("sugerencia producto"));
    }
}
