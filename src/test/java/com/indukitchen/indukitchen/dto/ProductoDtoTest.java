package com.indukitchen.indukitchen.dto;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductoDtoTest {

    @Test
    void constructor_and_accessors_work() {
        var dto = new ProductoDto(
                1L,
                "Plancha",
                "Plancha de cocina",
                new BigDecimal("12.50"),
                5,
                1.2,
                "plancha.png"
        );

        assertEquals(1L, dto.id());
        assertEquals("Plancha", dto.nombre());
        assertEquals("Plancha de cocina", dto.descripcion());
        assertEquals(new BigDecimal("12.50"), dto.precio());
        assertEquals(5, dto.existencia());
        assertEquals(1.2, dto.peso());
        assertEquals("plancha.png", dto.imagen());
    }

    @Test
    void equals_hashcode_and_toString_work() {
        var a = new ProductoDto(1L, "Horno", "Horno eléctrico",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");
        var b = new ProductoDto(1L, "Horno", "Horno eléctrico",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");
        var c = new ProductoDto(2L, "Horno", "Horno eléctrico",
                new BigDecimal("100.00"), 10, 8.5, "horno.jpg");

        assertEquals(a, b, "Mismos valores deben ser iguales");
        assertEquals(a.hashCode(), b.hashCode(), "hash debe coincidir para iguales");
        assertNotEquals(a, c, "Diferente id => objetos distintos");

        var s = a.toString();
        assertTrue(s.contains("ProductoDto"), "toString debe incluir el nombre del record");
        assertTrue(s.contains("id=1"), "toString debe listar campos y valores");
        assertTrue(s.contains("nombre=Horno"));
        assertTrue(s.contains("precio=100.00"));
    }

    @Test
    void null_fields_are_allowed_on_reference_types() {
        var dto = new ProductoDto(
                3L,
                null,   // nombre
                null,   // descripcion
                null,   // precio
                null,   // existencia
                null,   // peso
                null    // imagen
        );

        assertEquals(3L, dto.id());
        assertNull(dto.nombre());
        assertNull(dto.descripcion());
        assertNull(dto.precio());
        assertNull(dto.existencia());
        assertNull(dto.peso());
        assertNull(dto.imagen());
    }

    @Test
    void changing_any_field_changes_equality() {
        var base = new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img");

        assertNotEquals(base, new ProductoDto(9L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "img"));
        assertNotEquals(base, new ProductoDto(1L, "Z", "B", new BigDecimal("1.00"), 1, 0.5, "img"));
        assertNotEquals(base, new ProductoDto(1L, "A", "Z", new BigDecimal("1.00"), 1, 0.5, "img"));
        assertNotEquals(base, new ProductoDto(1L, "A", "B", new BigDecimal("9.99"), 1, 0.5, "img"));
        assertNotEquals(base, new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 2, 0.5, "img"));
        assertNotEquals(base, new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 9.9, "img"));
        assertNotEquals(base, new ProductoDto(1L, "A", "B", new BigDecimal("1.00"), 1, 0.5, "other"));
    }

    @Test
    void bigdecimal_scale_affects_equality() {
        var a = new ProductoDto(1L, "X", "Y", new BigDecimal("10.0"), 1, 1.0, "i");
        var b = new ProductoDto(1L, "X", "Y", new BigDecimal("10.00"), 1, 1.0, "i");

        // BigDecimal.equals considera la escala, por lo que 10.0 != 10.00
        assertNotEquals(a, b, "Escala distinta en BigDecimal debe afectar la igualdad del record");
    }
}
