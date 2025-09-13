package com.indukitchen.indukitchen.persistence.mapper;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(source = "cedula", target = "cedula")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "direccion", target = "direccion")
    @Mapping(source = "correo", target = "correo")
    @Mapping(source = "telefono", target = "telefono")
//    @Mapping(source = "createdAt", target = "createdAt")
//    @Mapping(source = "updatedAt", target = "updatedAt")

    // Mapear la lista de entidades CarritoEntity a lista de ids (String)
    //@Mapping(source = "carritos", target = "carritos", qualifiedByName = "carritosToIds")
    ClienteDto toDto(ClienteEntity entity);

    @Mapping(target = "cedula", ignore = true)
    List<ClienteDto> toDto(Iterable<ClienteEntity> entities);

    // Invertimos la configuración anterior, pero ignoramos campos que gestiona JPA o relaciones complejas
    @InheritInverseConfiguration
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(target = "carritos", ignore = true) // manejar asociación carritos en el servicio
    ClienteEntity toEntity(ClienteDto dto);

    /**
     * Convierte List<CarritoEntity> -> List<String> con los ids de carrito.
     * Ajusta `getId()` si tu CarritoEntity tiene otro nombre para el id.
     */
//    @Named("carritosToIds")
//    default List<String> carritosToIds(List<CarritoEntity> carritos) {
//        if (carritos == null) return null;
//        return carritos.stream()
//                .map(c -> String.valueOf(c.getId())) // usa el getId() apropiado de CarritoEntity
//                .collect(Collectors.toList());
//    }
}
