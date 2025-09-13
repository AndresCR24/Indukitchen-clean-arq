package com.indukitchen.indukitchen.persistence.mapper;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    //@Mapping(source = "id", target = "id")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "descripcion", target = "descripcion")
    @Mapping(source = "precio", target = "precio")
    @Mapping(source = "existencia", target = "existencia")
    @Mapping(source = "peso", target = "peso")
    @Mapping(source = "imagen", target = "imagen")
    //@Mapping(source = "createdAt", target = "createdAt")
    //@Mapping(source = "updatedAt", target = "updatedAt")
    //@Mapping(source = "detalles", target = "detalles")
    ProductoDto toDto(ProductoEntity entity);

    List<ProductoDto> toDto(Iterable<ProductoEntity> entities);

    //Con esta anotacion integra todos los mappers
    @InheritInverseConfiguration
    //@Mapping(source = "detalle", target = "detalle", qualifiedByName = "detalleToString")
    ProductoEntity toEntity(ProductoDto dto);
}

