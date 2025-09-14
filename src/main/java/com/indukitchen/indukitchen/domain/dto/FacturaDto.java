package com.indukitchen.indukitchen.domain.dto;

public record FacturaDto(
        long id,
        long idCarrito,
        Integer idMetodoPago
        //LocalDateTime createdAt,
        //LocalDateTime updatedAt
) {
}
