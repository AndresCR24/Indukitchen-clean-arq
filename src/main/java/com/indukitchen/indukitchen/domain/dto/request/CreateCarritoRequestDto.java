package com.indukitchen.indukitchen.domain.dto.request;

import java.util.List;

public record CreateCarritoRequestDto(
        String idCliente,
        List<Long> productoIds
) {

}
