package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class CarritoDtoTest {

    @Test
    void constructor_and_accessors_work() {
        var productos = List.<ProductoDto>of(); // lista vacía para no depender de la forma de ProductoDto
        var dto = new CarritoDto(1L, "CLI-9", productos);

        assertEquals(1L, dto.id());
        assertEquals("CLI-9", dto.idCliente());
        assertSame(productos, dto.productos(), "El record debe exponer la misma referencia recibida");
    }

    @Test
    void equals_and_hashcode_use_all_components() {
        var a = new CarritoDto(5L, "C", List.of());
        var b = new CarritoDto(5L, "C", List.of());
        var c = new CarritoDto(6L, "C", List.of());

        assertEquals(a, b, "Dos records con los mismos componentes deben ser iguales");
        assertEquals(a.hashCode(), b.hashCode(), "hashCode coherente con equals");
        assertNotEquals(a, c, "Cambiar un componente rompe la igualdad");
    }

    @Test
    void toString_contains_component_values() {
        var dto = new CarritoDto(10L, "CLI-1", List.of());
        var s = dto.toString();

        assertTrue(s.contains("CarritoDto"), "Debe incluir el nombre del record");
        assertTrue(s.contains("id=10"), "Debe incluir el id");
        assertTrue(s.contains("idCliente=CLI-1"), "Debe incluir el idCliente");
    }

    @Test
    void productos_can_be_null_when_not_validated_elsewhere() {
        var dto = new CarritoDto(2L, "C2", null);
        assertNull(dto.productos());
    }

    @Test
    @DisplayName("Record mantiene la referencia de la lista (shallow copy)")
    void list_reference_is_not_copied() {
        // Arrange
        ArrayList<ProductoDto> mutableList = new ArrayList<>();
        ProductoDto producto = new ProductoDto(1L, "Arroz", "Premium",
                new BigDecimal("5000"), 100, 1.0, "arroz.jpg");

        // Act
        CarritoDto dto = new CarritoDto(3L, "C3", mutableList);
        mutableList.add(producto);

        // Assert: el record mantiene la misma referencia de lista
        assertThat(dto.productos())
                .isSameAs(mutableList)
                .hasSize(1)
                .containsExactly(producto);
    }

    @Test
    @DisplayName("Record con múltiples productos")
    void record_with_multiple_products() {
        // Arrange
        ProductoDto producto1 = new ProductoDto(1L, "Arroz", "Premium",
                new BigDecimal("5000"), 100, 1.0, "arroz.jpg");
        ProductoDto producto2 = new ProductoDto(2L, "Frijol", "Rojo",
                new BigDecimal("3500"), 50, 0.5, "frijol.jpg");
        ProductoDto producto3 = new ProductoDto(3L, "Pasta", "Italiana",
                new BigDecimal("8000"), 200, 0.5, "pasta.jpg");

        List<ProductoDto> productos = List.of(producto1, producto2, producto3);

        // Act
        CarritoDto dto = new CarritoDto(20L, "CLI-5", productos);

        // Assert
        assertThat(dto.productos())
                .hasSize(3)
                .containsExactly(producto1, producto2, producto3);
        assertThat(dto.id()).isEqualTo(20L);
        assertThat(dto.idCliente()).isEqualTo("CLI-5");
    }

    @Test
    @DisplayName("Record con ID cero y idCliente vacío")
    void record_with_zero_id_and_empty_cliente() {
        // Arrange & Act
        CarritoDto dto = new CarritoDto(0L, "", List.of());

        // Assert
        assertThat(dto.id()).isZero();
        assertThat(dto.idCliente()).isEmpty();
        assertThat(dto.productos()).isEmpty();
    }

    @Test
    @DisplayName("Record con IDs negativos")
    void record_with_negative_id() {
        // Arrange & Act
        CarritoDto dto = new CarritoDto(-1L, "CLI-NEG", List.of());

        // Assert
        assertThat(dto.id())
                .isNegative()
                .isEqualTo(-1L);
        assertThat(dto.idCliente()).isEqualTo("CLI-NEG");
    }
}
