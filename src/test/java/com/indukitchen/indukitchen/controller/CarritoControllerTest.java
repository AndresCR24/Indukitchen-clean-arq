package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.request.ProcesarCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.domain.service.CarritoService;
import com.indukitchen.indukitchen.web.controller.CarritoController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
@Import(TestJacksonConfig.class)
class CarritoControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    CarritoService carritoService;

    @Test
    void getAll_returns200AndEmptyArray() throws Exception {
        given(carritoService.getAllCarritos()).willReturn(Collections.emptyList());

        mvc.perform(get("/carritos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void getById_found_returns200() throws Exception {
        given(carritoService.getById(1L)).willReturn(mock(CarritoDto.class)); // serializa {}

        mvc.perform(get("/carritos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        given(carritoService.getById(999L)).willReturn(null);

        mvc.perform(get("/carritos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void add_creates_returns201() throws Exception {
        given(carritoService.add(ArgumentMatchers.any(CreateCarritoRequestDto.class)))
                .willReturn(mock(CarritoDto.class));

        mvc.perform(post("/carritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void update_returns200() throws Exception {
        given(carritoService.update(eq(5L), any(UpdateCarritoDto.class)))
                .willReturn(mock(CarritoDto.class));

        mvc.perform(put("/carritos/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void delete_returns200() throws Exception {
        willDoNothing().given(carritoService).delete(3L);

        mvc.perform(delete("/carritos/3"))
                .andExpect(status().isOk());

        then(carritoService).should().delete(3L);
    }

    @Test
    void procesar_returns201() throws Exception {
        given(carritoService.procesarCarrito(any(ProcesarCarritoRequestDto.class)))
                .willReturn(mock(CarritoDto.class));

        mvc.perform(post("/carritos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
