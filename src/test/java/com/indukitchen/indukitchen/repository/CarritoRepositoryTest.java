package com.indukitchen.indukitchen.repository;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.domain.repository.CarritoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.anyLong;

// SUT (Servicio minimalista) para demostrar el uso del repositorio con mocks.
// Si ya tienes un CarritoService real, puedes borrar esta clase interna y
// apuntar los tests a tu servicio existente.
@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    // ---------- Servicio mínimo bajo prueba ----------
    static class CarritoService {
        private final CarritoRepository repo;
        CarritoService(CarritoRepository repo) { this.repo = repo; }
        List<CarritoDto> getAll() { return repo.getAll(); }
        CarritoDto getById(long id) { return repo.getById(id); }
        CarritoDto create(CreateCarritoRequestDto req) { return repo.save(req); }
        CarritoDto update(long id, UpdateCarritoDto dto) { return repo.update(id, dto); }
        void delete(long id) { repo.delete(id); }
    }

    @Mock CarritoRepository repo;
    @InjectMocks CarritoService service;

    @Test
    @DisplayName("getAll: devuelve lista del repositorio")
    void getAll_returns_list() {
        // Arrange
        var dto1 = mock(CarritoDto.class);
        var dto2 = mock(CarritoDto.class);
        given(repo.getAll()).willReturn(List.of(dto1, dto2));

        // Act
        var result = service.getAll();

        // Assert
        assertThat(result).hasSize(2).containsExactly(dto1, dto2);
        then(repo).should().getAll();
        then(repo).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("getById: retorna el carrito cuando existe")
    void getById_returns_when_present() {
        // Arrange
        var dto = mock(CarritoDto.class);
        given(repo.getById(10L)).willReturn(dto);

        // Act
        var result = service.getById(10L);

        // Assert
        assertThat(result).isSameAs(dto);
        then(repo).should().getById(10L);
        then(repo).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("create: delega en repo.save y retorna el CarritoDto creado")
    void create_delegates_to_repo_save() {
        // Arrange
        var req = mock(CreateCarritoRequestDto.class);
        var created = mock(CarritoDto.class);
        given(repo.save(req)).willReturn(created);

        // Act
        var result = service.create(req);

        // Assert
        assertThat(result).isSameAs(created);
        then(repo).should().save(req);
        then(repo).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("update: delega en repo.update con id y dto")
    void update_delegates_to_repo_update() {
        // Arrange
        var upd = mock(UpdateCarritoDto.class);
        var updated = mock(CarritoDto.class);
        given(repo.update(5L, upd)).willReturn(updated);

        // Act
        var result = service.update(5L, upd);

        // Assert
        assertThat(result).isSameAs(updated);
        then(repo).should().update(5L, upd);
        then(repo).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("delete: delega en repo.delete")
    void delete_delegates_to_repo_delete() {
        // Arrange
        willDoNothing().given(repo).delete(anyLong());

        // Act
        service.delete(77L);

        // Assert
        then(repo).should().delete(77L);
        then(repo).shouldHaveNoMoreInteractions();
    }
}
