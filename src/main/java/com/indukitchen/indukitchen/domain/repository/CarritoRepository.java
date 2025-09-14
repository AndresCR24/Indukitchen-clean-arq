package com.indukitchen.indukitchen.domain.repository;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;

import java.util.List;

public interface CarritoRepository {
    List<CarritoDto> getAll();
    CarritoDto getById(long id);
    CarritoDto save(CreateCarritoRequestDto createCarritoRequestDto);
    CarritoDto update(long id, UpdateCarritoDto updateCarritoDto);
    //boolean exists(long id);
    void delete(long id);
}
