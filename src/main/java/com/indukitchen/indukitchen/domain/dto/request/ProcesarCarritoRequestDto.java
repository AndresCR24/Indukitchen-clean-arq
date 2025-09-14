package com.indukitchen.indukitchen.domain.dto.request;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;

import java.util.List;

public record ProcesarCarritoRequestDto(
        ClienteDto cliente,
        List<Long> productoIds,
        Integer idMetodoPago,   // puede ser null si aún no seleccionas MP
        String emailTo,         // si null, se usa cliente.correo
        String emailSubject,    // opcional
        String emailText        // opcional
) {
}
