package com.indukitchen.indukitchen.domain.service;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.repository.ClienteRepository;
import dev.langchain4j.agent.tool.Tool;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    @Tool("Busca todos los usuarios que existan dentro de la plataforma")
    public List<ClienteDto> getAllClientes() {
        return this.clienteRepository.getAll();
    }

    public ClienteDto getById(String cedula) {
        return this.clienteRepository.getById(cedula);
    }

    public ClienteDto add(ClienteDto clienteDto) {
        return this.clienteRepository.save(clienteDto);
    }

    public ClienteDto update(String cedula, UpdateClienteDto updateClienteDto) {
        return this.clienteRepository.update(cedula, updateClienteDto);
    }

    public void delete(String cedula) {
        this.clienteRepository.delete(cedula);
    }

}

