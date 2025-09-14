package com.indukitchen.indukitchen.persistence.mapper;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
//import com.indukitchen.indukitchen.persistence.entity.DetalleEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarritoMapper {

    //@Mapping(source = "id", target = "id")
    @Mapping(source = "idCliente", target = "idCliente")
    //@Mapping(source = "createdAt", target = "createdAt")
    //@Mapping(source = "updatedAt", target = "updatedAt")
    //
    // @Mapping(source = "detalles", target = "detalles")

    CarritoDto toDto(CarritoEntity entity);

    List<CarritoDto> toDto(Iterable<CarritoEntity> entities);

    //Con esta anotación integra todos los mappers
    @InheritInverseConfiguration
    @Mapping(target = "cliente", ignore = true) // el DTO no expone 'cliente'
    CarritoEntity toEntity(CarritoDto dto);

//    @AfterMapping
//    default void setBackRefs(CarritoDto dto, @MappingTarget CarritoEntity entity) {
//        List<DetalleEntity> detalles = entity.getDetalles();
//        if (detalles != null) {
//            for (DetalleEntity d : detalles) {
//                d.setCarrito(entity);
//            }
//        }
//    }
}
