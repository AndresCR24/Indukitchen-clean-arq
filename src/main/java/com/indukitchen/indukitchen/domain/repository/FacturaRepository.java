package com.indukitchen.indukitchen.domain.repository;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository {
    List<FacturaDto> getAll();
    FacturaDto getById(long id);
    FacturaDto save(FacturaDto facturaDto);
    //FacturaDto update(long id, UpdateCarritoDto updateCarritoDto);
    //boolean exists(long id);
    void delete(long id);


//    @Override
//    @EntityGraph(attributePaths = {
//            "carritoFactura",
//            "carritoFactura.cliente",
//            "carritoFactura.productos"
//    })
//    Optional<FacturaEntity> findById(Long id);
}
