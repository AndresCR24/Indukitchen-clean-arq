package com.indukitchen.indukitchen.service;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.repository.ProductoRepository;
import com.indukitchen.indukitchen.domain.service.ProductoService;
import dev.langchain4j.agent.tool.Tool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Method;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    ProductoService productoService;

    @Test
    void getAll_delegatesToRepository() {
        var list = List.of(mock(ProductoDto.class));
        given(productoRepository.getAll()).willReturn(list);

        var res = productoService.getAll();

        assertSame(list, res);
        then(productoRepository).should().getAll();
    }

    @Test
    void getById_delegatesToRepository() {
        var dto = mock(ProductoDto.class);
        given(productoRepository.getById(42L)).willReturn(dto);

        var res = productoService.getById(42L);

        assertSame(dto, res);
        then(productoRepository).should().getById(42L);
    }

    @Test
    void add_delegatesToRepository() {
        var input = mock(ProductoDto.class);
        var saved = mock(ProductoDto.class);
        given(productoRepository.save(input)).willReturn(saved);

        var res = productoService.add(input);

        assertSame(saved, res);
        then(productoRepository).should().save(input);
    }

    @Test
    void delete_delegatesToRepository() {
        willDoNothing().given(productoRepository).delete(7L);

        productoService.delete(7L);

        then(productoRepository).should().delete(7L);
    }

    @Test
    void getAll_hasToolAnnotation() throws Exception {
        Method m = ProductoService.class.getMethod("getAll");
        assertTrue(m.isAnnotationPresent(Tool.class), "getAll debería estar anotado con @Tool");

        Tool tool = m.getAnnotation(Tool.class);

        // En langchain4j recientes Tool.value() -> String[]
        String[] parts = tool.value();
        assertNotNull(parts, "El value() de @Tool no debería ser null");
        String joined = String.join(" ", parts).replaceAll("\\s+", " ").trim();

        assertFalse(joined.isBlank(), "La descripción de @Tool no debería estar vacía");
        // Opcional: valida contenido aproximado sin ser frágil
        assertTrue(joined.toLowerCase().contains("productos"),
                "La descripción debería mencionar 'productos'");
    }

}
