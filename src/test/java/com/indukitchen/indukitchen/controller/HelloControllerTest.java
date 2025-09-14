package com.indukitchen.indukitchen.controller;

import com.indukitchen.indukitchen.TestJacksonConfig;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.web.controller.HelloController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
@Import(TestJacksonConfig.class)
@TestPropertySource(properties = "spring.application.name=InduKitchenApp")
class HelloControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean
    IndukitchenAiService aiService;

    @Test
    void hello_returnsGreetingFromService() throws Exception {
        given(aiService.generateGreeting(anyString())).willReturn("hola ");

        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hola "));
    }
}

