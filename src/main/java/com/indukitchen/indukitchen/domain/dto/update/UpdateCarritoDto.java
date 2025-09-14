package com.indukitchen.indukitchen.domain.dto.update;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;

import java.util.List;

public record UpdateCarritoDto(

        long id,
        String idCliente,
        List<ProductoDto> productoDtos
        //LocalDateTime createdAt,
        //LocalDateTime updatedAt,
        //ClienteDto cliente
) {
}
