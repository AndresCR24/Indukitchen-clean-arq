package com.indukitchen.indukitchen.domain.repository;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.dto.UpdateProductoDto;

import java.util.List;

public interface ProductoRepository {

    List<ProductoDto> getAll();
    ProductoDto getById(long id);
    ProductoDto save(ProductoDto movieDto);
    //ProductoDto update(long id, UpdateProductoDto updateMovieDto);
    void delete(long id);
}
