package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.service.FacturaService;
import com.indukitchen.indukitchen.web.controller.FacturaController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
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

@WebMvcTest(FacturaController.class)
@Import(TestJacksonConfig.class)
class FacturaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    FacturaService facturaService;

    @Test
    void getAll_returns200AndEmptyArray() throws Exception {
        given(facturaService.getAllFacturas()).willReturn(Collections.emptyList());

        mvc.perform(get("/facturas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getById_found_returns200() throws Exception {
        given(facturaService.getById(1L)).willReturn(mock(FacturaDto.class));

        mvc.perform(get("/facturas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        given(facturaService.getById(999L)).willReturn(null);

        mvc.perform(get("/facturas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void add_creates_returns201() throws Exception {
        given(facturaService.add(any(FacturaDto.class))).willReturn(mock(FacturaDto.class));

        mvc.perform(post("/facturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void delete_returns200() throws Exception {
        willDoNothing().given(facturaService).delete(5L);

        mvc.perform(delete("/facturas/5"))
                .andExpect(status().isOk());

        then(facturaService).should().delete(5L);
    }

    @Test
    void getFacturaPdf_returns200PdfInline() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("PDF".getBytes(StandardCharsets.UTF_8));

        given(facturaService.generateFacturaPdf(7L)).willReturn(baos);

        // Tu mapping actual es "/{id}/{pdf}". Para simular "/facturas/7/pdf":
        mvc.perform(get("/facturas/7/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("inline")))
                .andExpect(content().bytes(baos.toByteArray()));
    }

    @Test
    void getFacturaPdf_notFound_returns404() throws Exception {
        ByteArrayOutputStream empty = new ByteArrayOutputStream(); // length 0
        given(facturaService.generateFacturaPdf(8L)).willReturn(empty);

        mvc.perform(get("/facturas/8/pdf"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendFacturaEmail_returns202() throws Exception {
        willDoNothing().given(facturaService).generarEnviarFactura(10L);

        mvc.perform(post("/facturas/10/email"))
                .andExpect(status().isAccepted());

        then(facturaService).should().generarEnviarFactura(10L);
    }
}

