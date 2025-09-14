package com.indukitchen.indukitchen.persistence.repository;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.domain.repository.CarritoRepository;
import com.indukitchen.indukitchen.persistence.crud.CrudCarritoEntity;
import com.indukitchen.indukitchen.persistence.crud.CrudProductoEntity;
import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import com.indukitchen.indukitchen.persistence.mapper.CarritoMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CarritoEntityRepository implements CarritoRepository {

    private final CrudCarritoEntity crudCarritoEntity;
    private final CrudProductoEntity crudProductoEntity;
    private final CarritoMapper carritoMapper;

    public CarritoEntityRepository(CrudCarritoEntity crudCarritoEntity, CrudProductoEntity crudProductoEntity, CarritoMapper carritoMapper) {
        this.crudCarritoEntity = crudCarritoEntity;
        this.crudProductoEntity = crudProductoEntity;
        this.carritoMapper = carritoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoDto> getAll() {
        // findAll() ya viene con productos por @EntityGraph
        return this.carritoMapper.toDto(this.crudCarritoEntity.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CarritoDto getById(long id) {
        CarritoEntity carritoEntity = this.crudCarritoEntity.findById(id).orElse(null);
        return this.carritoMapper.toDto(carritoEntity);
    }

    @Override
    @Transactional
    public CarritoDto save(CreateCarritoRequestDto createCarritoRequestDto) {
        // mapear lo básico
        CarritoEntity carrito = new CarritoEntity();
        carrito.setIdCliente(createCarritoRequestDto.idCliente());
        // cargar productos si se enviaron ids
        if (createCarritoRequestDto.productoIds() != null && !createCarritoRequestDto.productoIds().isEmpty()) {
            List<ProductoEntity> productos = this.crudProductoEntity.findAllById(createCarritoRequestDto.productoIds());
            // validación opcional: todos los IDs existen
            Set<Long> encontrados = productos.stream().map(ProductoEntity::getId).collect(Collectors.toSet());
            List<Long> faltantes = createCarritoRequestDto.productoIds().stream().filter(id -> !encontrados.contains(id)).toList();
            if (!faltantes.isEmpty()) {
                throw new IllegalArgumentException("Productos no encontrados: " + faltantes);
            }
            carrito.setProductos(productos);
        }

        CarritoEntity saved = this.crudCarritoEntity.save(carrito);
        return this.carritoMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CarritoDto update(long id, UpdateCarritoDto updateCarritoDto) {
        CarritoEntity carritoEntity = this.crudCarritoEntity.findById(id).orElse(null);
        if (carritoEntity == null) return null;

        // ⚠️ No cambies la PK:
        // carritoEntity.setId(updateCarritoDto.id());  // <- evita esto

        // Actualiza campos permitidos
        carritoEntity.setIdCliente(updateCarritoDto.idCliente());

        // Si también quieres poder reemplazar productos en update, agrega aquí la lógica:
        // List<ProductoEntity> productos = crudProductoEntity.findAllById(updateCarritoDto.productoIds());
        // carritoEntity.setProductos(productos);

        return this.carritoMapper.toDto(this.crudCarritoEntity.save(carritoEntity));
    }

    @Override
    @Transactional
    public void delete(long id) {
        this.crudCarritoEntity.deleteById(id);
    }
}
