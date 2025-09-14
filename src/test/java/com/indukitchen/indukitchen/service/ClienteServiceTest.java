package com.indukitchen.indukitchen.service;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.repository.ClienteRepository;
import com.indukitchen.indukitchen.domain.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @InjectMocks
    ClienteService clienteService;

    @Test
    void getAllClientes_delegatesToRepository() {
        var list = List.of(mock(ClienteDto.class));
        given(clienteRepository.getAll()).willReturn(list);

        var res = clienteService.getAllClientes();

        assertSame(list, res);
        then(clienteRepository).should().getAll();
    }

    @Test
    void getById_delegatesToRepository() {
        var dto = mock(ClienteDto.class);
        given(clienteRepository.getById("C-123")).willReturn(dto);

        var res = clienteService.getById("C-123");

        assertSame(dto, res);
        then(clienteRepository).should().getById("C-123");
    }

    @Test
    void add_delegatesToRepository() {
        var input = mock(ClienteDto.class);
        var saved = mock(ClienteDto.class);
        given(clienteRepository.save(input)).willReturn(saved);

        var res = clienteService.add(input);

        assertSame(saved, res);
        then(clienteRepository).should().save(input);
    }

    @Test
    void update_delegatesToRepository() {
        var upd = mock(UpdateClienteDto.class);
        var updated = mock(ClienteDto.class);
        given(clienteRepository.update("C-999", upd)).willReturn(updated);

        var res = clienteService.update("C-999", upd);

        assertSame(updated, res);
        then(clienteRepository).should().update("C-999", upd);
    }

    @Test
    void delete_delegatesToRepository() {
        BDDMockito.willDoNothing().given(clienteRepository).delete("C-777");

        clienteService.delete("C-777");

        then(clienteRepository).should().delete("C-777");
    }
}
