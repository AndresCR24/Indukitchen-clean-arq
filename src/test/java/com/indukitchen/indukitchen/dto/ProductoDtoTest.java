package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para ProductoDto (Record):
 * - AAA (Arrange / Act / Assert)
 * - AssertJ para aserciones fluidas
 * - Verifica comportamiento de records (equals, hashCode, toString, accessors)
 * - Mantiene tests rápidos y aislados (FIRST)
 */
class ProductoDtoTest {

    @Test
    @DisplayName("Constructor y accessors funcionan correctamente")
    void constructor_and_accessors_work() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(
                1L,
                "Plancha",
                "Plancha de cocina",
                new BigDecimal("12.50"),
                5,
                1.2,
                "plancha.png"
        );

        // Assert
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nombre()).isEqualTo("Plancha");
        assertThat(dto.descripcion()).isEqualTo("Plancha de cocina");
        assertThat(dto.precio()).isEqualByComparingTo(new BigDecimal("12.50"));
        assertThat(dto.existencia()).isEqualTo(5);
        assertThat(dto.peso()).isEqualTo(1.2);
        assertThat(dto.imagen()).isEqualTo("plancha.png");
    }

    @Test
    @DisplayName("Record con todos los campos válidos")
    void record_with_all_valid_fields() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(
                100L,
                "Horno Industrial",
                "Horno eléctrico de alta capacidad",
                new BigDecimal("5000.00"),
                10,
                50.5,
                "horno.jpg"
        );

        // Assert
        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.nombre()).isEqualTo("Horno Industrial");
        assertThat(dto.descripcion()).contains("alta capacidad");
        assertThat(dto.precio()).isEqualByComparingTo("5000.00");
        assertThat(dto.existencia()).isEqualTo(10);
        assertThat(dto.peso()).isEqualTo(50.5);
        assertThat(dto.imagen()).isEqualTo("horno.jpg");
    }

    @Test
    @DisplayName("equals() compara todos los componentes del record")
    void equals_compares_all_components() {
        // Arrange
        ProductoDto a = new ProductoDto(1L, "Horno", "Horno eléctrico",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");
        ProductoDto b = new ProductoDto(1L, "Horno", "Horno eléctrico",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");
        ProductoDto c = new ProductoDto(2L, "Horno", "Horno eléctrico",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");

        // Assert
        assertThat(a)
                .isEqualTo(b)
                .isNotEqualTo(c);
    }

    @Test
    @DisplayName("hashCode() es coherente con equals()")
    void hashcode_is_consistent_with_equals() {
        // Arrange
        ProductoDto a = new ProductoDto(1L, "Horno", "Desc",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");
        ProductoDto b = new ProductoDto(1L, "Horno", "Desc",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");

        // Assert
        assertThat(a).hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("toString() contiene información del record")
    void toString_contains_record_info() {
        // Arrange
        ProductoDto dto = new ProductoDto(1L, "Horno", "Desc",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("ProductoDto")
                .contains("id=1")
                .contains("nombre=Horno")
                .contains("precio=100.00");
    }

    @Test
    @DisplayName("Campos de referencia pueden ser null")
    void null_fields_are_allowed_on_reference_types() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(3L, null, null, null, null, null, null);

        // Assert
        assertThat(dto.id()).isEqualTo(3L);
        assertThat(dto.nombre()).isNull();
        assertThat(dto.descripcion()).isNull();
        assertThat(dto.precio()).isNull();
        assertThat(dto.existencia()).isNull();
        assertThat(dto.peso()).isNull();
        assertThat(dto.imagen()).isNull();
    }

    @Test
    @DisplayName("Cambiar id afecta la igualdad")
    void changing_id_changes_equality() {
        // Arrange
        ProductoDto base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");
        ProductoDto different = new ProductoDto(9L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar nombre afecta la igualdad")
    void changing_nombre_changes_equality() {
        // Arrange
        ProductoDto base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");
        ProductoDto different = new ProductoDto(1L, "Z", "B", new BigDecimal("1.00"), 1, 0.5, "img");

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar descripción afecta la igualdad")
    void changing_descripcion_changes_equality() {
        // Arrange
        ProductoDto base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");
        ProductoDto different = new ProductoDto(1L, "A", "Z", new BigDecimal("1.00"), 1, 0.5, "img");

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar precio afecta la igualdad")
    void changing_precio_changes_equality() {
        // Arrange
        ProductoDto base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");
        ProductoDto different = new ProductoDto(1L, "A", "B", new BigDecimal("9.99"), 1, 0.5, "img");

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar existencia afecta la igualdad")
    void changing_existencia_changes_equality() {
        // Arrange
        ProductoDto base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");
        ProductoDto different = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 2, 0.5, "img");

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar peso afecta la igualdad")
    void changing_peso_changes_equality() {
        // Arrange
        ProductoDto base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");
        ProductoDto different = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 9.9, "img");

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("Cambiar imagen afecta la igualdad")
    void changing_imagen_changes_equality() {
        // Arrange
        ProductoDto base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");
        ProductoDto different = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "other");

        // Assert
        assertThat(base).isNotEqualTo(different);
    }

    @Test
    @DisplayName("BigDecimal con diferente escala afecta la igualdad")
    void bigdecimal_scale_affects_equality() {
        // Arrange
        ProductoDto a = new ProductoDto(1L, "X", "Y", new BigDecimal("10.0"), 1, 1.0, "i");
        ProductoDto b = new ProductoDto(1L, "X", "Y", new BigDecimal("10.00"), 1, 1.0, "i");

        // Assert: BigDecimal.equals considera la escala, por lo que 10.0 != 10.00
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("Record con precio cero")
    void record_with_zero_price() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(1L, "Gratis", "Desc", BigDecimal.ZERO, 10, 1.0, "img");

        // Assert
        assertThat(dto.precio()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Record con existencia cero")
    void record_with_zero_stock() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(1L, "Agotado", "Desc", new BigDecimal("100"), 0, 1.0, "img");

        // Assert
        assertThat(dto.existencia()).isZero();
    }

    @Test
    @DisplayName("Record con peso cero")
    void record_with_zero_weight() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(1L, "Digital", "Desc", new BigDecimal("100"), 10, 0.0, "img");

        // Assert
        assertThat(dto.peso()).isZero();
    }

    @Test
    @DisplayName("Record con valores negativos")
    void record_with_negative_values() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(-1L, "Test", "Desc",
                new BigDecimal("-10.00"), -5, -1.0, "img");

        // Assert
        assertThat(dto.id()).isNegative();
        assertThat(dto.precio()).isNegative();
        assertThat(dto.existencia()).isNegative();
        assertThat(dto.peso()).isNegative();
    }

    @Test
    @DisplayName("Record con precio muy grande")
    void record_with_large_price() {
        // Arrange
        BigDecimal largePrice = new BigDecimal("999999999.99");

        // Act
        ProductoDto dto = new ProductoDto(1L, "Luxury", "Desc", largePrice, 1, 100.0, "img");

        // Assert
        assertThat(dto.precio()).isEqualByComparingTo(largePrice);
    }

    @Test
    @DisplayName("Record con caracteres especiales en texto")
    void record_with_special_characters() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(
                1L,
                "Ñoquis & Café",
                "Descripción con áccéntos y símbolos: @#$%",
                new BigDecimal("12.50"),
                10,
                2.5,
                "ñoquis_café.jpg"
        );

        // Assert
        assertThat(dto.nombre()).contains("Ñoquis", "&", "Café");
        assertThat(dto.descripcion()).contains("áccéntos", "@#$%");
        assertThat(dto.imagen()).contains("ñoquis_café");
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con null")
    void equals_returns_false_for_null() {
        // Arrange
        ProductoDto dto = new ProductoDto(1L, "Test", "Desc", new BigDecimal("10"), 1, 1.0, "img");

        // Assert
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("equals() retorna false cuando se compara con otro tipo")
    void equals_returns_false_for_different_type() {
        // Arrange
        ProductoDto dto = new ProductoDto(1L, "Test", "Desc", new BigDecimal("10"), 1, 1.0, "img");
        String otroTipo = "Not a ProductoDto";

        // Assert
        assertThat(dto).isNotEqualTo(otroTipo);
    }

    @Test
    @DisplayName("Dos records con todos los campos null (excepto id) son iguales")
    void two_records_with_all_null_fields_are_equal() {
        // Arrange
        ProductoDto a = new ProductoDto(5L, null, null, null, null, null, null);
        ProductoDto b = new ProductoDto(5L, null, null, null, null, null, null);

        // Assert
        assertThat(a)
                .isEqualTo(b)
                .hasSameHashCodeAs(b);
    }

    @Test
    @DisplayName("Record con descripción muy larga")
    void record_with_long_description() {
        // Arrange
        String longDesc = "Esta es una descripción muy larga ".repeat(10);

        // Act
        ProductoDto dto = new ProductoDto(1L, "Test", longDesc, new BigDecimal("10"), 1, 1.0, "img");

        // Assert
        assertThat(dto.descripcion())
                .hasSize(longDesc.length())
                .startsWith("Esta es una descripción");
    }

    @Test
    @DisplayName("Record con nombre vacío")
    void record_with_empty_name() {
        // Arrange & Act
        ProductoDto dto = new ProductoDto(1L, "", "Desc", new BigDecimal("10"), 1, 1.0, "img");

        // Assert
        assertThat(dto.nombre()).isEmpty();
    }

    @Test
    @DisplayName("toString() con campos null no causa error")
    void toString_with_null_fields_does_not_error() {
        // Arrange
        ProductoDto dto = new ProductoDto(1L, null, null, null, null, null, null);

        // Act
        String result = dto.toString();

        // Assert
        assertThat(result)
                .contains("ProductoDto", "id=1", "nombre=null");
    }
}
