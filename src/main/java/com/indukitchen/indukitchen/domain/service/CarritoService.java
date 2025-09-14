package com.indukitchen.indukitchen.domain.service;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.request.ProcesarCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.domain.repository.CarritoRepository;
import com.indukitchen.indukitchen.domain.repository.ClienteRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ClienteRepository clienteRepository;
    private final FacturaService facturaService;

    public CarritoService(CarritoRepository carritoRepository, ClienteRepository clienteRepository, FacturaService facturaService) {
        this.carritoRepository = carritoRepository;
        this.clienteRepository = clienteRepository;
        this.facturaService = facturaService;
    }

    //@Tool("Busca todos los usuarios que existan dentro de la plataforma")
    public List<CarritoDto> getAllCarritos() {
        return this.carritoRepository.getAll();
    }

    public CarritoDto getById(long id) {
        return this.carritoRepository.getById(id);
    }


    public CarritoDto add(CreateCarritoRequestDto createCarritoRequestDto) {
        return this.carritoRepository.save(createCarritoRequestDto);
    }

    public CarritoDto update(long id, UpdateCarritoDto updateCarritoDto) {
        return this.carritoRepository.update(id, updateCarritoDto);
    }

    public void delete(long id) {
        this.carritoRepository.delete(id);
    }

    /**
     * Flujo completo: guarda/actualiza cliente, crea carrito con productos,
     * crea factura y envía email con PDF. Si falla el email, NO hace rollback.
     */
    @Transactional(noRollbackFor = MessagingException.class)
    public CarritoDto procesarCarrito(ProcesarCarritoRequestDto procesarCarritoRequestDto) {
        // 1) Validaciones mínimas
        if (procesarCarritoRequestDto == null || procesarCarritoRequestDto.cliente() == null)
            throw new IllegalArgumentException("Cliente es obligatorio");
        if (procesarCarritoRequestDto.productoIds() == null || procesarCarritoRequestDto.productoIds().isEmpty())
            throw new IllegalArgumentException("Debe enviar al menos un producto");

        ClienteDto clienteDto = procesarCarritoRequestDto.cliente();

        // 2) Guardar/actualizar cliente
        ClienteDto clienteSaved = this.clienteRepository.save(clienteDto);

        // 3) Crear carrito con ManyToMany (productoIds)
        CreateCarritoRequestDto createReq =
                new CreateCarritoRequestDto(clienteSaved.cedula(), procesarCarritoRequestDto.productoIds());
        CarritoDto carritoCreado = this.carritoRepository.save(createReq);

        // 4) Crear factura
        Integer idMetodoPago = procesarCarritoRequestDto.idMetodoPago(); // puede ser null
        FacturaDto facturaCreada = this.facturaService.add(
                new FacturaDto(0L, carritoCreado.id(), idMetodoPago)
        );

        // 5) Envío de factura por email (si falla, NO revierte DB)
        String to = (procesarCarritoRequestDto.emailTo() != null && !procesarCarritoRequestDto.emailTo().isBlank())
                ? procesarCarritoRequestDto.emailTo()
                : (clienteSaved.correo() != null ? clienteSaved.correo() : null);

        if (to != null && !to.isBlank()) {

            // Genera el PDF y envía por correo usando el id de la factura
            this.facturaService.generarEnviarFactura(facturaCreada.id());

            // Nota: el método de FacturaService maneja internamente las excepciones de envío.
        }

        return carritoCreado;
    }
}


