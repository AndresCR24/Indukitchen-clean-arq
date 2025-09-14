package com.indukitchen.indukitchen.persistence.repository;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.repository.ProductoRepository;
import com.indukitchen.indukitchen.persistence.crud.CrudProductoEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import com.indukitchen.indukitchen.persistence.mapper.ProductoMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductoEntityRepository implements ProductoRepository {

    private final CrudProductoEntity crudProductoEntity;
    private final ProductoMapper productoMapper;

    public ProductoEntityRepository(CrudProductoEntity crudProductoEntity, ProductoMapper productoMapper) {
        this.crudProductoEntity = crudProductoEntity;
        this.productoMapper = productoMapper;
    }


    @Override
    public List<ProductoDto> getAll() {
        return this.productoMapper.toDto(this.crudProductoEntity.findAll());
    }

    @Override
    public ProductoDto getById(long id) {
        ProductoEntity productoEntity = this.crudProductoEntity.findById(id).orElse(null);
        return this.productoMapper.toDto(productoEntity);
    }

    @Override
    public ProductoDto save(ProductoDto productoDto) {

//        if (this.crudProductoEntity.findFirstByTitulo(productoDto.nombre()) != null) {
//            throw new MovieAlredyExistException(movieDto.title());
//        }

        ProductoEntity productoEntity = this.productoMapper.toEntity(productoDto);
        //productoEntity.setDescripcion("Disponible");
        return this.productoMapper.toDto(this.crudProductoEntity.save(productoEntity));
    }

//    @Override
//    public ProductoDto update(long id, UpdateProductoDto updateMovieDto) {
//        ProductoEntity movieEntity = this.crudProductoEntity.findById(id).orElse(null);
//
//        if (movieEntity == null) return null;
//
//        //this.movieMapper.updateEntityFromDto(updateMovieDto, movieEntity);
//        movieEntity.set(UpdateProductoDto.title());
//        movieEntity.setFechaEstreno(updateMovieDto.releaseDate());
//        movieEntity.setClasificacion(BigDecimal.valueOf(updateMovieDto.rating()));
//        return this.movieMapper.toDto(this.crudMovieEntity.save(movieEntity));
//    }

    @Override
    public void delete(long id) {
        this.crudProductoEntity.deleteById(id);
    }
}
