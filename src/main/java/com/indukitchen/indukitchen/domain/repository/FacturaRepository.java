package com.indukitchen.indukitchen.domain.repository;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;

import java.util.List;

public interface FacturaRepository {
    List<FacturaDto> getAll();
    FacturaDto getById(long id);
    FacturaDto save(FacturaDto facturaDto);
    //FacturaDto update(long id, UpdateCarritoDto updateCarritoDto);
    //boolean exists(long id);
    void delete(long id);
}
