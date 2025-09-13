/*
package com.indukitchen.indukitchen.web.controller;

import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final String plataform;
    private final IndukitchenAiService aiService;

    public HelloController(@Value("${spring.application.name}") String plataform, IndukitchenAiService aiService) {
        this.aiService = aiService;
        this.plataform = plataform;
    }

    @GetMapping("/hello")
    public String hello() {
        return this.aiService.generateGreeting(plataform);
    }


}

*/
