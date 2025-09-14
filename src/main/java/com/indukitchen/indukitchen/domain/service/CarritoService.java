package com.indukitchen.indukitchen.domain.service;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.domain.repository.CarritoRepository;
import com.indukitchen.indukitchen.domain.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository, ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }


    //@Tool("Busca todos los usuarios que existan dentro de la plataforma")
    public List<CarritoDto> getAllCarritos() {
        return this.carritoRepository.getAll();
    }

    public CarritoDto getById(long id) {
        return this.carritoRepository.getById(id);
    }


    public CarritoDto add(CreateCarritoRequestDto createCarritoRequestDto) {
        return this.carritoRepository.save(createCarritoRequestDto);
    }

//    public CarritoDto add(CarritoDto carritoDto) {
//        return this.carritoRepository.save(carritoDto);
//    }

    public CarritoDto update(long id, UpdateCarritoDto updateCarritoDto) {
        return this.carritoRepository.update(id, updateCarritoDto);
    }

    public void delete(long id) {
        this.carritoRepository.delete(id);
    }

}
