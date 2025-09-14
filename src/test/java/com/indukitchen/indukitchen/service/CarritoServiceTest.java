package com.indukitchen.indukitchen.service;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.request.ProcesarCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.domain.repository.CarritoRepository;
import com.indukitchen.indukitchen.domain.repository.ClienteRepository;
import com.indukitchen.indukitchen.domain.service.CarritoService;
import com.indukitchen.indukitchen.domain.service.FacturaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    CarritoRepository carritoRepository;
    @Mock
    ClienteRepository clienteRepository;
    @Mock
    FacturaService facturaService;

    @InjectMocks
    CarritoService carritoService;




    // ---------------- Delegaciones simples ----------------

    @Test
    void getAllCarritos_delegatesToRepository() {
        given(carritoRepository.getAll()).willReturn(List.of());

        var res = carritoService.getAllCarritos();

        assertNotNull(res);
        then(carritoRepository).should().getAll();
    }

    @Test
    void getById_delegatesToRepository() {
        var carrito = mock(CarritoDto.class);
        given(carritoRepository.getById(7L)).willReturn(carrito);

        var res = carritoService.getById(7L);

        assertSame(carrito, res);
        then(carritoRepository).should().getById(7L);
    }

    @Test
    void add_delegatesToRepository() {
        var req = new CreateCarritoRequestDto("C-123", List.of(1L, 2L));
        var creado = mock(CarritoDto.class);
        given(carritoRepository.save(req)).willReturn(creado);

        var res = carritoService.add(req);

        assertSame(creado, res);
        then(carritoRepository).should().save(req);
    }

    @Test
    void update_delegatesToRepository() {
        var upd = mock(UpdateCarritoDto.class);
        var actualizado = mock(CarritoDto.class);
        given(carritoRepository.update(5L, upd)).willReturn(actualizado);

        var res = carritoService.update(5L, upd);

        assertSame(actualizado, res);
        then(carritoRepository).should().update(5L, upd);
    }

    @Test
    void delete_delegatesToRepository() {
        willDoNothing().given(carritoRepository).delete(9L);

        carritoService.delete(9L);

        then(carritoRepository).should().delete(9L);
    }

    // ---------------- procesarCarrito ----------------

    @Test
    void procesarCarrito_usesClienteCorreo_whenEmailToIsBlank(){
        var cliente = mock(ClienteDto.class);
        given(cliente.cedula()).willReturn("C-123");
        given(cliente.correo()).willReturn("cliente@correo.com");

        var carritoCreado = mock(CarritoDto.class);
        given(carritoCreado.id()).willReturn(321L);

        var req = mock(ProcesarCarritoRequestDto.class);
        given(req.cliente()).willReturn(cliente);
        given(req.productoIds()).willReturn(List.of(1L));
        given(req.idMetodoPago()).willReturn(null);
        given(req.emailTo()).willReturn("   "); // en blanco

        given(clienteRepository.save(cliente)).willReturn(cliente);
        given(carritoRepository.save(any(CreateCarritoRequestDto.class))).willReturn(carritoCreado);
        given(facturaService.add(any(FacturaDto.class))).willReturn(new FacturaDto(777L, 321L, null));
        willDoNothing().given(facturaService).generarEnviarFactura(777L);

        var res = carritoService.procesarCarrito(req);

        assertSame(carritoCreado, res);
        then(facturaService).should().generarEnviarFactura(777L); // se envía usando correo del cliente
    }

    @Test
    void procesarCarrito_noEmailProvided_doesNotSendEmail()  {
        var cliente = mock(ClienteDto.class);
        given(cliente.cedula()).willReturn("C-123");
        given(cliente.correo()).willReturn(null); // sin correo guardado

        var carritoCreado = mock(CarritoDto.class);
        given(carritoCreado.id()).willReturn(999L);

        var req = mock(ProcesarCarritoRequestDto.class);
        given(req.cliente()).willReturn(cliente);
        given(req.productoIds()).willReturn(List.of(1L, 2L));
        given(req.idMetodoPago()).willReturn(5);
        given(req.emailTo()).willReturn(null); // sin emailTo

        given(clienteRepository.save(cliente)).willReturn(cliente);
        given(carritoRepository.save(any(CreateCarritoRequestDto.class))).willReturn(carritoCreado);
        given(facturaService.add(any(FacturaDto.class))).willReturn(new FacturaDto(888L, 999L, 5));

        var res = carritoService.procesarCarrito(req);

        assertSame(carritoCreado, res);
        then(facturaService).should(never()).generarEnviarFactura(anyLong()); // no se envía
    }

    @Test
    void procesarCarrito_nullCliente_throwsIllegalArgument() {
        var req = mock(ProcesarCarritoRequestDto.class);
        given(req.cliente()).willReturn(null); // prioridad: valida cliente primero

        var ex = assertThrows(IllegalArgumentException.class, () -> carritoService.procesarCarrito(req));
        assertTrue(ex.getMessage().toLowerCase().contains("cliente"));
    }

    @Test
    void procesarCarrito_emptyProductos_throwsIllegalArgument() {
        var req = mock(ProcesarCarritoRequestDto.class);
        given(req.cliente()).willReturn(mock(ClienteDto.class));
        given(req.productoIds()).willReturn(List.of()); // vacío

        var ex = assertThrows(IllegalArgumentException.class, () -> carritoService.procesarCarrito(req));
        assertTrue(ex.getMessage().toLowerCase().contains("producto"));
    }

    // Nota: NO probamos una MessagingException lanzada por generarEnviarFactura(...)
    // porque ese método no la declara; Mockito no permite lanzar checked exceptions
    // en métodos que no las declaran. Si quisieras simular un fallo inesperado,
    // podrías usar una RuntimeException y verificar que se propaga.
}
