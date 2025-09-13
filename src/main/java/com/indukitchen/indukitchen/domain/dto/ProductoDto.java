package com.indukitchen.indukitchen.domain.dto;

import java.math.BigDecimal;

public record ProductoDto(
        long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer existencia,
        Double peso,
        String imagen
        //LocalDateTime createdAt,
        //LocalDateTime updatedAt,
        //List<DetalleDto> detalles
) {
}
