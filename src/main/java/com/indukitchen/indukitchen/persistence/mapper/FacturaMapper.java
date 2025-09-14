package com.indukitchen.indukitchen.persistence.mapper;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacturaMapper {

    // Mapeo plano de campos (mismo estilo que ProductoMapper)
    @Mapping(source = "idCarrito", target = "idCarrito")
    @Mapping(source = "idMetodoPago", target = "idMetodoPago")
    //@Mapping(source = "createdAt", target = "createdAt")
    //@Mapping(source = "updatedAt", target = "updatedAt")
    FacturaDto toDto(FacturaEntity entity);

    List<FacturaDto> toDto(Iterable<FacturaEntity> entities);

    // Inversa. Ignoramos relaciones para evitar ciclos y cargas innecesarias.
    @InheritInverseConfiguration
    @Mapping(target = "carritoFactura", ignore = true)
    @Mapping(target = "metodoPagoFactura", ignore = true)
    FacturaEntity toEntity(FacturaDto dto);
}
