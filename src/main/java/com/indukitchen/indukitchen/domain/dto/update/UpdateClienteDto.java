package com.indukitchen.indukitchen.domain.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClienteDto(

        @NotBlank(message = "La cédula es obligatoria")
        String cedula,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 40, message = "El nombre no puede exceder 40 caracteres")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 40, message = "La dirección no puede exceder 40 caracteres")
        String direccion,

        @Email(message = "El correo debe tener un formato válido")
        @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 17, message = "El teléfono no puede exceder 18 caracteres")
        String telefono
) {
}
