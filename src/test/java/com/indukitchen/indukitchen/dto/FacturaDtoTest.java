package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FacturaDtoTest {

    @Test
    void constructor_and_accessors_work() {
        var dto = new FacturaDto(1L, 10L, 7);
        assertEquals(1L, dto.id());
        assertEquals(10L, dto.idCarrito());
        assertEquals(7, dto.idMetodoPago());
    }

    @Test
    void equals_hashcode_and_toString_work() {
        var a = new FacturaDto(1L, 10L, 7);
        var b = new FacturaDto(1L, 10L, 7);
        var c = new FacturaDto(2L, 10L, 7);

        assertEquals(a, b, "Mismos valores deben ser iguales");
        assertEquals(a.hashCode(), b.hashCode(), "hash debe coincidir para iguales");
        assertNotEquals(a, c, "Diferente id => objetos distintos");

        var s = a.toString();
        assertTrue(s.contains("FacturaDto"), "toString debe incluir el nombre del record");
        assertTrue(s.contains("id=1"), "toString debe listar campos y valores");
        assertTrue(s.contains("idCarrito=10"));
        assertTrue(s.contains("idMetodoPago=7"));
    }

    @Test
    void idMetodoPago_can_be_null() {
        var dto = new FacturaDto(5L, 100L, null);
        assertEquals(5L, dto.id());
        assertEquals(100L, dto.idCarrito());
        assertNull(dto.idMetodoPago(), "idMetodoPago debe permitir null");
    }

    @Test
    void changing_any_field_changes_equality() {
        var base = new FacturaDto(1L, 2L, 3);

        assertNotEquals(base, new FacturaDto(9L, 2L, 3));
        assertNotEquals(base, new FacturaDto(1L, 9L, 3));
        assertNotEquals(base, new FacturaDto(1L, 2L, 9));
    }
}
