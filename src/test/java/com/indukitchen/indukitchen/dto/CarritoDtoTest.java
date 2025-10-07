package com.indukitchen.indukitchen.dto;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void list_reference_is_not_copied_shallow_semantics() {
        var mutable = new ArrayList<ProductoDto>();
        var dto = new CarritoDto(3L, "C3", mutable);

        // añadimos un elemento (puede ser null; no dependemos de la forma de ProductoDto)
        mutable.add(null);
        assertEquals(1, dto.productos().size(), "El record mantiene la misma referencia de lista");
    }
}


