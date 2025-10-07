package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.SuggestProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para SuggestProductDto (Record):
 * - AAA (Arrange / Act / Assert)
 * - AssertJ para aserciones fluidas
 * - Verifica comportamiento de records (equals, hashCode, toString, accessors)
 * - Mantiene tests rápidos y aislados (FIRST)
 */
class SuggestProductDtoTest {

    @Test
    @DisplayName("Constructor y accessor funcionan correctamente")
    void constructor_and_accessor_work() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto("productos orgánicos");

        // Assert
        assertThat(dto.userPreferences()).isEqualTo("productos orgánicos");
    }

    @Test
    @DisplayName("Record con preferencias vacías")
    void record_with_empty_preferences() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto("");

        // Assert
        assertThat(dto.userPreferences()).isEmpty();
    }

    @Test
    @DisplayName("Record con preferencias null")
    void record_with_null_preferences() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto(null);

        // Assert
        assertThat(dto.userPreferences()).isNull();
    }

    @Test
    @DisplayName("Record con preferencias largas")
    void record_with_long_preferences() {
        // Arrange
        String longPreferences = "Busco productos de alta calidad, orgánicos, libres de gluten, "
                + "veganos, con certificación internacional, precio económico, entrega rápida, "
                + "empaque ecológico y que sean de producción local";

        // Act
        SuggestProductDto dto = new SuggestProductDto(longPreferences);

        // Assert
        assertThat(dto.userPreferences()).isEqualTo(longPreferences);
        assertThat(dto.userPreferences()).hasSize(longPreferences.length());
    }

    @Test
    @DisplayName("equals() compara correctamente las preferencias")
    void equals_compares_preferences() {
        // Arrange
        SuggestProductDto a = new SuggestProductDto("arroz");
        SuggestProductDto b = new SuggestProductDto("arroz");
        SuggestProductDto c = new SuggestProductDto("frijol");

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    @DisplayName("hashCode() es coherente con equals()")
    void hashcode_is_consistent_with_equals() {
        // Arrange
        SuggestProductDto a = new SuggestProductDto("pasta");
        SuggestProductDto b = new SuggestProductDto("pasta");

        // Assert
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("hashCode() cambia cuando cambian las preferencias")
    void hashcode_changes_when_preferences_change() {
        // Arrange
        SuggestProductDto a = new SuggestProductDto("opción1");
        SuggestProductDto b = new SuggestProductDto("opción2");

        // Assert
        assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("toString() contiene información del record")
    void toString_contains_record_info() {
        // Arrange
        SuggestProductDto dto = new SuggestProductDto("productos frescos");

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("SuggestProductDto")
                .contains("userPreferences=productos frescos");
    }

    @Test
    @DisplayName("toString() con preferencias null")
    void toString_with_null_preferences() {
        // Arrange
        SuggestProductDto dto = new SuggestProductDto(null);

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("SuggestProductDto")
                .contains("userPreferences=null");
    }

    @Test
    @DisplayName("Record con caracteres especiales en preferencias")
    void record_with_special_characters() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto("Café & Té con áccéntos: @#$%");

        // Assert
        assertThat(dto.userPreferences())
                .contains("Café")
                .contains("&")
                .contains("áccéntos")
                .contains("@#$%");
    }

    @Test
    @DisplayName("Dos records con null son iguales")
    void two_records_with_null_are_equal() {
        // Arrange
        SuggestProductDto a = new SuggestProductDto(null);
        SuggestProductDto b = new SuggestProductDto(null);

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("Dos records con cadena vacía son iguales")
    void two_records_with_empty_string_are_equal() {
        // Arrange
        SuggestProductDto a = new SuggestProductDto("");
        SuggestProductDto b = new SuggestProductDto("");

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con null")
    void equals_returns_false_for_null() {
        // Arrange
        SuggestProductDto dto = new SuggestProductDto("test");

        // Assert
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con otro tipo")
    void equals_returns_false_for_different_type() {
        // Arrange
        SuggestProductDto dto = new SuggestProductDto("test");
        String otroTipo = "Not a SuggestProductDto";

        // Assert
        assertThat(dto).isNotEqualTo(otroTipo);
    }

    @Test
    @DisplayName("equals() es reflexivo")
    void equals_is_reflexive() {
        // Arrange
        SuggestProductDto dto = new SuggestProductDto("test");

        // Act
        boolean result = dto.equals(dto);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("equals() es simétrico")
    void equals_is_symmetric() {
        // Arrange
        SuggestProductDto a = new SuggestProductDto("test");
        SuggestProductDto b = new SuggestProductDto("test");

        // Act & Assert
        assertThat(a.equals(b)).isEqualTo(b.equals(a));
    }

    @Test
    @DisplayName("equals() es transitivo")
    void equals_is_transitive() {
        // Arrange
        SuggestProductDto a = new SuggestProductDto("test");
        SuggestProductDto b = new SuggestProductDto("test");
        SuggestProductDto c = new SuggestProductDto("test");

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(b).isEqualTo(c);
        assertThat(a).isEqualTo(c);
    }

    @Test
    @DisplayName("Record con preferencias en diferentes idiomas")
    void record_with_multilingual_preferences() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto(
                "Busco: Rice (English), Riz (Français), 米 (日本語), Arroz (Español)"
        );

        // Assert
        assertThat(dto.userPreferences())
                .contains("Rice")
                .contains("Riz")
                .contains("米")
                .contains("Arroz");
    }

    @Test
    @DisplayName("Record con solo espacios")
    void record_with_only_whitespace() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto("   ");

        // Assert
        assertThat(dto.userPreferences()).isEqualTo("   ");
        assertThat(dto.userPreferences()).isNotEmpty();
        assertThat(dto.userPreferences()).isBlank();
    }

    @Test
    @DisplayName("Record con saltos de línea y tabulaciones")
    void record_with_newlines_and_tabs() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto("línea1\nlínea2\ttabulado");

        // Assert
        assertThat(dto.userPreferences())
                .contains("\n")
                .contains("\t")
                .contains("línea1")
                .contains("línea2");
    }

    @Test
    @DisplayName("Record con emojis")
    void record_with_emojis() {
        // Arrange & Act
        SuggestProductDto dto = new SuggestProductDto("🍕 Pizza 🍔 Burger 🍰 Cake");

        // Assert
        assertThat(dto.userPreferences())
                .contains("🍕")
                .contains("🍔")
                .contains("🍰")
                .contains("Pizza");
    }
}

