package com.indukitchen.indukitchen;


import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestJacksonConfig {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer disableFailOnEmptyBeans() {
        return builder -> builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}

