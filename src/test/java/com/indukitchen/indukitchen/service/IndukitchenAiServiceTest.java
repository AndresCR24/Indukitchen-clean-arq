package com.indukitchen.indukitchen.service;

import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import org.junit.jupiter.api.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class IndukitchenAiServiceAnnotationsTest {

    private static String normalize(String s) {
        return s == null ? "" : s.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    // Overload para anotaciones que devuelven String[]
    private static String normalize(String[] parts) {
        if (parts == null) return "";
        return normalize(String.join(" ", parts));
    }

    @Test
    void class_isAnnotatedWithAiService() {
        assertTrue(IndukitchenAiService.class.isAnnotationPresent(AiService.class),
                "@AiService debe estar presente en la interfaz");
    }

    @Test
    void generateGreeting_hasUserMessage_withExpectedContent() throws Exception {
        Method m = IndukitchenAiService.class.getMethod("generateGreeting", String.class);
        UserMessage um = m.getAnnotation(UserMessage.class);
        assertNotNull(um, "generateGreeting debe tener @UserMessage");

        String prompt = normalize(um.value()); // <-- ahora acepta String[]
        assertTrue(prompt.contains("saludo al usuario de la plataforma"),
                "El prompt debe mencionar saludo al usuario");
        assertTrue(prompt.contains("mejores en estos productos de cocina industrial en colombia"),
                "El prompt debe afirmar liderazgo en productos de cocina industrial en Colombia");
        assertTrue(prompt.contains("no te pases de 30 palabras"),
                "El prompt debe limitar a 30 palabras");
    }

    @Test
    void generateProductSuggestion_methodHasSystemMessage_andParamHasUserMessage() throws Exception {
        Method m = IndukitchenAiService.class.getMethod("generateProductSuggestion", String.class);

        SystemMessage sm = m.getAnnotation(SystemMessage.class);
        assertNotNull(sm, "generateProductSuggestion debe tener @SystemMessage");
        String sys = normalize(sm.value()); // <-- String[]

        assertTrue(sys.contains("experto en productos industriales de cocina"),
                "System prompt debe describir expertise");
        assertTrue(sys.contains("máximo 3 productos") || sys.contains("maximo 3 productos"),
                "Debe limitar a máximo 3 productos");
        assertTrue(sys.contains("no incluyas productos que estén por fuera de la plataforma indukitchen"),
                "Debe restringir al catálogo de Indukitchen");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertEquals(1, paramAnns.length, "Debe haber un único parámetro");
        boolean hasUserMessageOnParam = Arrays.stream(paramAnns[0])
                .anyMatch(a -> a.annotationType().equals(UserMessage.class));
        assertTrue(hasUserMessageOnParam, "El parámetro debe tener @UserMessage");
    }

    @Test
    void generateClienteSuggestion_methodHasSystemMessage_andParamHasUserMessage() throws Exception {
        Method m = IndukitchenAiService.class.getMethod("generateClienteSuggestion", String.class);

        SystemMessage sm = m.getAnnotation(SystemMessage.class);
        assertNotNull(sm, "generateClienteSuggestion debe tener @SystemMessage");
        String sys = normalize(sm.value()); // <-- String[]

        assertTrue(sys.contains("experto en productos industriales de cocina"),
                "System prompt debe describir expertise");
        assertTrue(sys.contains("clientes de la empresa son los mejores"),
                "Debe orientar a recomendar mejores clientes");
        assertTrue(sys.contains("no incluyas clientes que estén por fuera de la plataforma indukitchen"),
                "Debe restringir a clientes de Indukitchen");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertEquals(1, paramAnns.length, "Debe haber un único parámetro");
        boolean hasUserMessageOnParam = Arrays.stream(paramAnns[0])
                .anyMatch(a -> a.annotationType().equals(UserMessage.class));
        assertTrue(hasUserMessageOnParam, "El parámetro debe tener @UserMessage");
    }
}


