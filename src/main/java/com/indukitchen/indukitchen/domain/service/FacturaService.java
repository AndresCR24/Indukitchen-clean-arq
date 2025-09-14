package com.indukitchen.indukitchen.domain.service;
import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.repository.ClienteRepository;
import com.indukitchen.indukitchen.domain.repository.FacturaRepository;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }


    //@Tool("Busca todos los usuarios que existan dentro de la plataforma")
    public List<FacturaDto> getAllFacturas() {
        return this.facturaRepository.getAll();
    }

    public FacturaDto getById(long id) {
        return this.facturaRepository.getById(id);
    }

    public FacturaDto add(FacturaDto facturaDto) {
        return this.facturaRepository.save(facturaDto);
    }

    //Pensar si en la logica del negocio dejaremos actualizar facturas
//    public ClienteDto update(long id, UpdateFacturaDto updateFacturaDto) {
//        return this.facturaRepository.update(id, updateFacturaDto);
//    }

    public void delete(long id) {
        this.facturaRepository.delete(id);
    }

}


