package com.indukitchen.indukitchen.domain.repository;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;

import java.util.List;

public interface ClienteRepository {
    List<ClienteDto> getAll();
    ClienteDto getById(String cedula);
    ClienteDto save(ClienteDto clienteDto);
    ClienteDto update(String cedula, UpdateClienteDto updateClienteDto);
    void delete(String cedula);
}
