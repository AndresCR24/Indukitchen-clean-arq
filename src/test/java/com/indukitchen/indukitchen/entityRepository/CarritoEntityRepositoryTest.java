package com.indukitchen.indukitchen.entityRepository;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import com.indukitchen.indukitchen.persistence.mapper.CarritoMapperImpl;
import com.indukitchen.indukitchen.persistence.repository.CarritoEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({CarritoEntityRepository.class, CarritoMapperImpl.class})
class CarritoEntityRepositoryIT {

    @Autowired
    EntityManager em;

    @Autowired
    CarritoEntityRepository repository;

    // ========= helpers =========

    private ClienteEntity newCliente(String cedula) {
        var c = new ClienteEntity();
        c.setCedula(cedula);
        c.setNombre("Nombre " + cedula);
        c.setDireccion("Direccion " + cedula);
        c.setCorreo(cedula.toLowerCase() + "@dom.com");
        c.setTelefono("3000000000");
        c.setPassword("pwd");
        c.setLocked(false);
        c.setDisabled(false);
        em.persist(c);
        return c;
    }

    private ProductoEntity newProducto(String nombre, BigDecimal precio) {
        var p = new ProductoEntity();
        p.setNombre(nombre);
        p.setPrecio(precio);
        em.persist(p);
        return p;
    }

    private CarritoEntity newCarrito(String idCliente, List<ProductoEntity> productos) {
        var ce = new CarritoEntity();
        ce.setIdCliente(idCliente);
        if (productos != null) ce.setProductos(productos);
        em.persist(ce);
        return ce;
    }

    // ========= tests =========

    @Test
    @DisplayName("save(): crea carrito con productos válidos y los mapea a DTO")
    void save_creates_cart_with_products() {
        // given
        newCliente("CLI-1");
        var p1 = newProducto("Plancha", new BigDecimal("10.00"));
        var p2 = newProducto("Horno", new BigDecimal("20.00"));
        em.flush();

        var req = new CreateCarritoRequestDto("CLI-1", List.of(p1.getId(), p2.getId()));

        // when
        CarritoDto dto = repository.save(req);

        // then
        assertNotNull(dto);
        assertTrue(dto.id() > 0);
        assertEquals("CLI-1", dto.idCliente());
        assertEquals(2, dto.productos().size());
    }

    @Test
    @DisplayName("save(): lanza IllegalArgumentException si vienen IDs de productos inexistentes")
    void save_throws_when_product_ids_missing() {
        // given
        newCliente("CLI-1");
        var p1 = newProducto("Plancha", new BigDecimal("10.00"));
        em.flush();

        var req = new CreateCarritoRequestDto("CLI-1", List.of(p1.getId(), 999999L));

        // when / then
        var ex = assertThrows(IllegalArgumentException.class, () -> repository.save(req));
        assertTrue(ex.getMessage().contains("Productos no encontrados"));
    }

    @Test
    @DisplayName("getById(): devuelve carrito con sus productos")
    void getById_returns_cart_with_products() {
        // given
        newCliente("CLI-2");
        var p1 = newProducto("Sartén", new BigDecimal("5.00"));
        var p2 = newProducto("Olla", new BigDecimal("7.00"));
        var saved = newCarrito("CLI-2", List.of(p1, p2));
        em.flush();
        em.clear();

        // when
        CarritoDto dto = repository.getById(saved.getId());

        // then
        assertNotNull(dto);
        assertEquals(saved.getId(), dto.id());
        assertEquals("CLI-2", dto.idCliente());
        assertEquals(2, dto.productos().size());
    }

    @Test
    @DisplayName("getAll(): devuelve lista de carritos")
    void getAll_returns_list() {
        // given
        newCliente("CLI-A");
        newCliente("CLI-B");
        var p = newProducto("Cuchillo", new BigDecimal("3.50"));
        newCarrito("CLI-A", List.of(p));
        newCarrito("CLI-B", List.of());
        em.flush();
        em.clear();

        // when
        var all = repository.getAll();

        // then
        assertNotNull(all);
        assertTrue(all.size() >= 2);
        assertTrue(all.stream().anyMatch(c -> c.idCliente().equals("CLI-A")));
        assertTrue(all.stream().anyMatch(c -> c.idCliente().equals("CLI-B")));
    }

    @Test
    @DisplayName("update(): cambia el idCliente respetando la FK")
    void update_changes_idCliente() {
        // given
        newCliente("CLI-OLD");
        newCliente("CLI-NEW");
        var carrito = newCarrito("CLI-OLD", List.of());
        em.flush();
        em.clear();

        // Usamos el record con 3 argumentos como definiste:
        var updateDto = new UpdateCarritoDto(
                carrito.getId(),         // id (no lo usa el repo, pero el record lo pide)
                "CLI-NEW",               // nuevo idCliente
                List.<ProductoDto>of()   // productos (no los actualiza este método)
        );

        // when
        var updated = repository.update(carrito.getId(), updateDto);

        // then
        assertNotNull(updated);
        assertEquals("CLI-NEW", updated.idCliente());

        // y verificamos en BD
        var reloaded = em.find(CarritoEntity.class, carrito.getId());
        assertEquals("CLI-NEW", reloaded.getIdCliente());
    }

    @Test
    @DisplayName("delete(): elimina por id sin excepciones")
    void delete_removes_cart() {
        // given
        newCliente("CLI-X");
        var cart = newCarrito("CLI-X", List.of());
        em.flush();
        long id = cart.getId();

        // when
        repository.delete(id);
        em.flush();
        em.clear();

        // then
        assertNull(em.find(CarritoEntity.class, id));
    }
}



