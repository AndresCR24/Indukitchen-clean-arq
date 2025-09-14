package com.indukitchen.indukitchen.domain.dto;

import java.util.List;

public record CarritoDto(

        long id,
        String idCliente,
        List<ProductoDto> productos
) {
}
