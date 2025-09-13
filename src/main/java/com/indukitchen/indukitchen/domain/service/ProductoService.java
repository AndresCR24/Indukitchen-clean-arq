package com.indukitchen.indukitchen.domain.service;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.dto.UpdateProductoDto;
import com.indukitchen.indukitchen.domain.repository.ProductoRepository;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }


    @Tool("Busca todos los productos que existan dentreo de la plataforma")
    public List<ProductoDto> getAll() {
        return this.productoRepository.getAll();
    }

    public ProductoDto getById(long id) {
        return this.productoRepository.getById(id);
    }

    public ProductoDto add(ProductoDto productoDto) {
        return this.productoRepository.save(productoDto);
    }

//    public ProductoDto update(long id, UpdateProductoDto updateProductoDto) {
//        return this.productoRepository.update(id, updateProductoDto);
//    }

    public void delete(long id) {
        this.productoRepository.delete(id);
    }

}

