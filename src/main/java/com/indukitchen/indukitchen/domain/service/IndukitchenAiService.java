package com.indukitchen.indukitchen.domain.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface IndukitchenAiService {

    @UserMessage("""
           Da un mensaje de saludo al usuario de la plataforma, di una frase donde se evidencie que somos
           los mejores en estos productos de cocina industrial en colombia no te pases de 30 palabras
           """)
    String generateGreeting(String plataform);

    @SystemMessage("""
            Eres un experto en productos industriales de cocina que recomienda personalizadamente según los gustos del usuario.
                        Debes recomendar máximo 3 productos.
                        No incluyas productos que estén por fuera de la plataforma Indukitchen.
            """)
    String generateMoviesSiggestion(@UserMessage String userMessage);
}
