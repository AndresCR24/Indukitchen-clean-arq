package com.indukitchen.indukitchen.domain.repository;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;

import java.util.List;

public interface ProductoRepository {
    List<ProductoDto> getAll();
    ProductoDto getById(long id);
    ProductoDto save(ProductoDto productoDto);
    //ProductoDto update(long id, UpdateProductoDto updateProductoDto);
    void delete(long id);
}
