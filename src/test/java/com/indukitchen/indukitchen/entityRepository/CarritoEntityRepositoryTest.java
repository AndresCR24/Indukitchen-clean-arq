package com.indukitchen.indukitchen.entityRepository;

import com.indukitchen.indukitchen.domain.dto.CarritoDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({CarritoEntityRepository.class, CarritoMapperImpl.class})
class CarritoEntityRepositoryTest {

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
        // Arrange
        newCliente("CLI-1");
        var p1 = newProducto("Plancha", new BigDecimal("10.00"));
        var p2 = newProducto("Horno", new BigDecimal("20.00"));
        em.flush();

        var req = new CreateCarritoRequestDto("CLI-1", List.of(p1.getId(), p2.getId()));

        // Act
        CarritoDto dto = repository.save(req);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isPositive();
        assertThat(dto.idCliente()).isEqualTo("CLI-1");
        assertThat(dto.productos()).hasSize(2);
    }

    @Test
    @DisplayName("save(): lanza IllegalArgumentException si vienen IDs de productos inexistentes")
    void save_throws_when_product_ids_missing() {
        // Arrange
        newCliente("CLI-1");
        var p1 = newProducto("Plancha", new BigDecimal("10.00"));
        em.flush();

        var req = new CreateCarritoRequestDto("CLI-1", List.of(p1.getId(), 999999L));

        // Act & Assert
        assertThatThrownBy(() -> repository.save(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Productos no encontrados");
    }

    @Test
    @DisplayName("getById(): devuelve carrito con sus productos")
    void getById_returns_cart_with_products() {
        // Arrange
        newCliente("CLI-2");
        var p1 = newProducto("Sartén", new BigDecimal("5.00"));
        var p2 = newProducto("Olla", new BigDecimal("7.00"));
        var saved = newCarrito("CLI-2", List.of(p1, p2));
        em.flush();
        em.clear();

        // Act
        CarritoDto dto = repository.getById(saved.getId());

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(saved.getId());
        assertThat(dto.idCliente()).isEqualTo("CLI-2");
        assertThat(dto.productos()).hasSize(2);
    }

    @Test
    @DisplayName("getAll(): devuelve lista de carritos")
    void getAll_returns_list() {
        // Arrange
        newCliente("CLI-A");
        newCliente("CLI-B");
        var p = newProducto("Cuchillo", new BigDecimal("3.50"));
        newCarrito("CLI-A", List.of(p));
        newCarrito("CLI-B", List.of());
        em.flush();
        em.clear();

        // Act
        var all = repository.getAll();

        // Assert (encadenado)
        assertThat(all)
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting(CarritoDto::idCliente)
                .contains("CLI-A", "CLI-B");
    }

    @Test
    @DisplayName("update(): cambia el idCliente respetando la FK")
    void update_changes_idCliente() {
        // Arrange
        newCliente("CLI-OLD");
        newCliente("CLI-NEW");
        var carrito = newCarrito("CLI-OLD", List.of());
        em.flush();
        em.clear();

        var updateDto = new UpdateCarritoDto(
                carrito.getId(),
                "CLI-NEW",
                List.of()
        );

        // Act
        var updated = repository.update(carrito.getId(), updateDto);

        // Assert
        assertThat(updated).isNotNull();
        assertThat(updated.idCliente()).isEqualTo("CLI-NEW");

        var reloaded = em.find(CarritoEntity.class, carrito.getId());
        assertThat(reloaded.getIdCliente()).isEqualTo("CLI-NEW");
    }

    @Test
    @DisplayName("delete(): elimina por id sin excepciones")
    void delete_removes_cart() {
        // Arrange
        newCliente("CLI-X");
        var cart = newCarrito("CLI-X", List.of());
        em.flush();
        long id = cart.getId();

        // Act
        repository.delete(id);
        em.flush();
        em.clear();

        // Assert
        assertThat(em.find(CarritoEntity.class, id)).isNull();
    }
}
