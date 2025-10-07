package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.SuggestClienteDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para SuggestClienteDto (Record):
 * - AAA (Arrange / Act / Assert)
 * - AssertJ para aserciones fluidas
 * - Verifica comportamiento de records (equals, hashCode, toString, accessors)
 * - Mantiene tests rápidos y aislados (FIRST)
 */
class SuggestClienteDtoTest {

    @Test
    @DisplayName("Constructor y accessor funcionan correctamente")
    void constructor_and_accessor_work() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto("clientes corporativos");

        // Assert
        assertThat(dto.userPreferences()).isEqualTo("clientes corporativos");
    }

    @Test
    @DisplayName("Record con preferencias vacías")
    void record_with_empty_preferences() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto("");

        // Assert
        assertThat(dto.userPreferences()).isEmpty();
    }

    @Test
    @DisplayName("Record con preferencias null")
    void record_with_null_preferences() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto(null);

        // Assert
        assertThat(dto.userPreferences()).isNull();
    }

    @Test
    @DisplayName("Record con preferencias largas")
    void record_with_long_preferences() {
        // Arrange
        String longPreferences = "Busco información sobre clientes potenciales en el sector "
                + "de restaurantes, con enfoque en establecimientos medianos y grandes, "
                + "que tengan alta rotación de productos y requieran suministros constantes, "
                + "preferiblemente con historial de pagos puntuales";

        // Act
        SuggestClienteDto dto = new SuggestClienteDto(longPreferences);

        // Assert
        assertThat(dto.userPreferences())
                .isEqualTo(longPreferences)
                .hasSize(longPreferences.length());
    }

    @Test
    @DisplayName("equals() compara correctamente las preferencias")
    void equals_compares_preferences() {
        // Arrange
        SuggestClienteDto a = new SuggestClienteDto("restaurantes");
        SuggestClienteDto b = new SuggestClienteDto("restaurantes");
        SuggestClienteDto c = new SuggestClienteDto("hoteles");

        // Assert
        assertThat(a)
                .isEqualTo(b)
                .isNotEqualTo(c);
    }

    @Test
    @DisplayName("hashCode() es coherente con equals()")
    void hashcode_is_consistent_with_equals() {
        // Arrange
        SuggestClienteDto a = new SuggestClienteDto("cafeterías");
        SuggestClienteDto b = new SuggestClienteDto("cafeterías");

        // Assert
        assertThat(a).hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("hashCode() cambia cuando cambian las preferencias")
    void hashcode_changes_when_preferences_change() {
        // Arrange
        SuggestClienteDto a = new SuggestClienteDto("sector A");
        SuggestClienteDto b = new SuggestClienteDto("sector B");

        // Assert
        assertThat(a).doesNotHaveSameHashCodeAs(b);
    }

    @Test
    @DisplayName("toString() contiene información del record")
    void toString_contains_record_info() {
        // Arrange
        SuggestClienteDto dto = new SuggestClienteDto("clientes premium");

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("SuggestClienteDto", "userPreferences=clientes premium");
    }

    @Test
    @DisplayName("toString() con preferencias null")
    void toString_with_null_preferences() {
        // Arrange
        SuggestClienteDto dto = new SuggestClienteDto(null);

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("SuggestClienteDto", "userPreferences=null");
    }

    @Test
    @DisplayName("Record con caracteres especiales en preferencias")
    void record_with_special_characters() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto("Clientes V.I.P. & empresas: @#$%");

        // Assert
        assertThat(dto.userPreferences())
                .contains("V.I.P.")
                .contains("&")
                .contains("empresas")
                .contains("@#$%");
    }

    @Test
    @DisplayName("Dos records con null son iguales")
    void two_records_with_null_are_equal() {
        // Arrange
        SuggestClienteDto a = new SuggestClienteDto(null);
        SuggestClienteDto b = new SuggestClienteDto(null);

        // Assert
        assertThat(a)
                .isEqualTo(b)
                .hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("Dos records con cadena vacía son iguales")
    void two_records_with_empty_string_are_equal() {
        // Arrange
        SuggestClienteDto a = new SuggestClienteDto("");
        SuggestClienteDto b = new SuggestClienteDto("");

        // Assert
        assertThat(a)
                .isEqualTo(b)
                .hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con null")
    void equals_returns_false_for_null() {
        // Arrange
        SuggestClienteDto dto = new SuggestClienteDto("test");

        // Assert
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con otro tipo")
    void equals_returns_false_for_different_type() {
        // Arrange
        SuggestClienteDto dto = new SuggestClienteDto("test");
        String otroTipo = "Not a SuggestClienteDto";

        // Assert
        assertThat(dto).isNotEqualTo(otroTipo);
    }

    @Test
    @DisplayName("equals() es reflexivo")
    void equals_is_reflexive() {
        // Arrange
        SuggestClienteDto dto = new SuggestClienteDto("test");

        // Act
        boolean result = dto.equals(dto);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("equals() es simétrico")
    void equals_is_symmetric() {
        // Arrange
        SuggestClienteDto a = new SuggestClienteDto("test");
        SuggestClienteDto b = new SuggestClienteDto("test");

        // Act & Assert
        assertThat(a.equals(b)).isEqualTo(b.equals(a));
    }

    @Test
    @DisplayName("equals() es transitivo")
    void equals_is_transitive() {
        // Arrange
        SuggestClienteDto a = new SuggestClienteDto("test");
        SuggestClienteDto b = new SuggestClienteDto("test");
        SuggestClienteDto c = new SuggestClienteDto("test");

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(b).isEqualTo(c);
        assertThat(a).isEqualTo(c);
    }

    @Test
    @DisplayName("Record con preferencias sobre diferentes tipos de clientes")
    void record_with_different_client_types() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto(
                "Restaurantes de 5 estrellas, hoteles boutique, cafeterías especializadas"
        );

        // Assert
        assertThat(dto.userPreferences())
                .contains("Restaurantes")
                .contains("hoteles")
                .contains("cafeterías");
    }

    @Test
    @DisplayName("Record con solo espacios")
    void record_with_only_whitespace() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto("   ");

        // Assert
        assertThat(dto.userPreferences())
                .isEqualTo("   ")
                .isNotEmpty()
                .isBlank();
    }

    @Test
    @DisplayName("Record con saltos de línea y tabulaciones")
    void record_with_newlines_and_tabs() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto("Tipo A:\n- Clientes frecuentes\tCriterio 1");

        // Assert
        assertThat(dto.userPreferences())
                .contains("\n", "\t", "Tipo A", "frecuentes");
    }

    @Test
    @DisplayName("Record con números y símbolos de moneda")
    void record_with_numbers_and_currency() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto("Clientes con compras > $50,000 USD");

        // Assert
        assertThat(dto.userPreferences())
                .contains("$50,000", "USD", ">");
    }

    @Test
    @DisplayName("Record con direcciones de correo electrónico")
    void record_with_email_addresses() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto(
                "Contactos: admin@restaurant.com, ventas@hotel.com"
        );

        // Assert
        assertThat(dto.userPreferences())
                .contains("admin@restaurant.com", "ventas@hotel.com", "@");
    }

    @Test
    @DisplayName("Record con URLs")
    void record_with_urls() {
        // Arrange & Act
        SuggestClienteDto dto = new SuggestClienteDto(
                "Sitios web: https://restaurant1.com, http://hotel2.com"
        );

        // Assert
        assertThat(dto.userPreferences())
                .contains("https://", "http://", "restaurant1.com");
    }
}
