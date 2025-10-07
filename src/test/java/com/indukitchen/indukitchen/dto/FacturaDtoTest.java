package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para FacturaDto (Record):
 * - AAA (Arrange / Act / Assert)
 * - AssertJ para aserciones fluidas
 * - Verifica comportamiento de records (equals, hashCode, toString, accessors)
 * - Mantiene tests rápidos y aislados (FIRST)
 */
class FacturaDtoTest {

    @Test
    @DisplayName("Constructor y accessors funcionan correctamente")
    void constructor_and_accessors_work() {
        // Arrange & Act
        FacturaDto dto = new FacturaDto(1L, 10L, 7);

        // Assert
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.idCarrito()).isEqualTo(10L);
        assertThat(dto.idMetodoPago()).isEqualTo(7);
    }

    @Test
    @DisplayName("Record con todos los campos válidos")
    void record_with_all_valid_fields() {
        // Arrange & Act
        FacturaDto dto = new FacturaDto(100L, 200L, 1);

        // Assert
        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.idCarrito()).isEqualTo(200L);
        assertThat(dto.idMetodoPago()).isEqualTo(1);
    }

    @Test
    @DisplayName("equals() compara todos los componentes del record")
    void equals_compares_all_components() {
        // Arrange
        FacturaDto a = new FacturaDto(1L, 10L, 7);
        FacturaDto b = new FacturaDto(1L, 10L, 7);
        FacturaDto c = new FacturaDto(2L, 10L, 7);

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    @DisplayName("hashCode() es coherente con equals()")
    void hashcode_is_consistent_with_equals() {
        // Arrange
        FacturaDto a = new FacturaDto(1L, 10L, 7);
        FacturaDto b = new FacturaDto(1L, 10L, 7);

        // Assert
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("hashCode() cambia cuando cambian los componentes")
    void hashcode_changes_when_components_change() {
        // Arrange
        FacturaDto a = new FacturaDto(1L, 10L, 7);
        FacturaDto b = new FacturaDto(2L, 10L, 7);

        // Assert
        assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("toString() contiene información del record")
    void toString_contains_record_info() {
        // Arrange
        FacturaDto dto = new FacturaDto(1L, 10L, 7);

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("FacturaDto")
                .contains("id=1")
                .contains("idCarrito=10")
                .contains("idMetodoPago=7");
    }

    @Test
    @DisplayName("idMetodoPago puede ser null")
    void idMetodoPago_can_be_null() {
        // Arrange & Act
        FacturaDto dto = new FacturaDto(5L, 100L, null);

        // Assert
        assertThat(dto.id()).isEqualTo(5L);
        assertThat(dto.idCarrito()).isEqualTo(100L);
        assertThat(dto.idMetodoPago()).isNull();
    }

    @Test
    @DisplayName("Cambiar id afecta la igualdad")
    void changing_id_changes_equality() {
        // Arrange
        FacturaDto base = new FacturaDto(1L, 2L, 3);
        FacturaDto different = new FacturaDto(9L, 2L, 3);

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar idCarrito afecta la igualdad")
    void changing_idCarrito_changes_equality() {
        // Arrange
        FacturaDto base = new FacturaDto(1L, 2L, 3);
        FacturaDto different = new FacturaDto(1L, 9L, 3);

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar idMetodoPago afecta la igualdad")
    void changing_idMetodoPago_changes_equality() {
        // Arrange
        FacturaDto base = new FacturaDto(1L, 2L, 3);
        FacturaDto different = new FacturaDto(1L, 2L, 9);

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Record con IDs cero")
    void record_with_zero_ids() {
        // Arrange & Act
        FacturaDto dto = new FacturaDto(0L, 0L, 0);

        // Assert
        assertThat(dto.id()).isZero();
        assertThat(dto.idCarrito()).isZero();
        assertThat(dto.idMetodoPago()).isZero();
    }

    @Test
    @DisplayName("Record con IDs negativos")
    void record_with_negative_ids() {
        // Arrange & Act
        FacturaDto dto = new FacturaDto(-1L, -5L, -10);

        // Assert
        assertThat(dto.id()).isNegative().isEqualTo(-1L);
        assertThat(dto.idCarrito()).isNegative().isEqualTo(-5L);
        assertThat(dto.idMetodoPago()).isNegative().isEqualTo(-10);
    }

    @Test
    @DisplayName("Record con IDs muy grandes")
    void record_with_large_ids() {
        // Arrange
        long largeId = Long.MAX_VALUE;

        // Act
        FacturaDto dto = new FacturaDto(largeId, largeId - 1, Integer.MAX_VALUE);

        // Assert
        assertThat(dto.id()).isEqualTo(Long.MAX_VALUE);
        assertThat(dto.idCarrito()).isEqualTo(Long.MAX_VALUE - 1);
        assertThat(dto.idMetodoPago()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con null")
    void equals_returns_false_for_null() {
        // Arrange
        FacturaDto dto = new FacturaDto(1L, 2L, 3);

        // Assert
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con otro tipo")
    void equals_returns_false_for_different_type() {
        // Arrange
        FacturaDto dto = new FacturaDto(1L, 2L, 3);
        String otroTipo = "Not a FacturaDto";

        // Assert
        assertThat(dto).isNotEqualTo(otroTipo);
    }

    @Test
    @DisplayName("equals() es reflexivo - retorna true cuando se compara consigo mismo")
    void equals_is_reflexive() {
        // Arrange
        FacturaDto dto = new FacturaDto(1L, 2L, 3);

        // Act
        boolean result = dto.equals(dto);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("equals() es simétrico")
    void equals_is_symmetric() {
        // Arrange
        FacturaDto a = new FacturaDto(1L, 2L, 3);
        FacturaDto b = new FacturaDto(1L, 2L, 3);

        // Act & Assert
        assertThat(a.equals(b)).isEqualTo(b.equals(a));
    }

    @Test
    @DisplayName("equals() es transitivo")
    void equals_is_transitive() {
        // Arrange
        FacturaDto a = new FacturaDto(1L, 2L, 3);
        FacturaDto b = new FacturaDto(1L, 2L, 3);
        FacturaDto c = new FacturaDto(1L, 2L, 3);

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(b).isEqualTo(c);
        assertThat(a).isEqualTo(c);
    }

    @Test
    @DisplayName("toString() incluye el nombre del record")
    void toString_includes_record_name() {
        // Arrange
        FacturaDto dto = new FacturaDto(1L, 2L, 3);

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result).startsWith("FacturaDto");
    }

    @Test
    @DisplayName("Record con idMetodoPago null en toString()")
    void toString_with_null_idMetodoPago() {
        // Arrange
        FacturaDto dto = new FacturaDto(1L, 2L, null);

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("FacturaDto")
                .contains("idMetodoPago=null");
    }

    @Test
    @DisplayName("Dos records con idMetodoPago null son iguales si otros campos coinciden")
    void two_records_with_null_idMetodoPago_are_equal() {
        // Arrange
        FacturaDto a = new FacturaDto(1L, 2L, null);
        FacturaDto b = new FacturaDto(1L, 2L, null);

        // Assert
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}

