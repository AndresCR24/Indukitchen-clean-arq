package com.indukitchen.indukitchen.entity;

import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CarritoEntityTest {

    @Autowired
    EntityManager em;

    /** Crea un cliente con todos los campos NOT NULL inicializados. */
    private ClienteEntity newCliente(String cedula, String nombre, String correo) {
        var c = new ClienteEntity();
        c.setCedula(cedula);
        c.setNombre(nombre);
        c.setCorreo(correo);
        // Campos NOT NULL según el esquema impreso por Hibernate:
        c.setTelefono("0000000");      // <= 17 chars
        c.setDireccion("Calle 1");     // <= 40 chars
        c.setPassword("secret");       // <= 200 chars
        c.setDisabled(false);
        c.setLocked(false);
        return c;
    }

    /** Crea un producto sin asignar ID manualmente (lo genera la DB). */
    private ProductoEntity newProducto(String nombre, BigDecimal precio) {
        var p = new ProductoEntity();
        // NO setId(...) porque es @GeneratedValue(IDENTITY)
        p.setNombre(nombre);
        p.setPrecio(precio);
        return p;
    }

    @Test
    void persist_and_load_basic_mappings_and_relations() {
        // Arrange: preparar cliente y productos existentes
        var cli = newCliente("C-1", "Ana", "ana@dominio.com");
        em.persist(cli);

        var p1 = newProducto("Plancha", new BigDecimal("12.50"));
        var p2 = newProducto("Campana", new BigDecimal("35.00"));
        em.persist(p1);
        em.persist(p2);

        // Arrange: crear carrito con idCliente y lista de productos
        var carrito = new CarritoEntity();
        carrito.setIdCliente("C-1");
        carrito.setProductos(List.of(p1, p2));
        em.persist(carrito);
        em.flush();
        em.clear();

        // Act: recuperar carrito desde la base de datos
        var found = em.find(CarritoEntity.class, carrito.getId());

        // Assert: verificar mapeos básicos y relaciones
        assertThat(found).isNotNull();
        assertThat(found.getIdCliente()).isEqualTo("C-1");
        assertThat(found.getProductos()).hasSize(2);

        // Assert: asociación ManyToOne debe ser LAZY inicialmente
        var util = Persistence.getPersistenceUtil();
        assertThat(util.isLoaded(found, "cliente"))
            .withFailMessage("Cliente debería ser LAZY inicialmente")
            .isFalse();

        // Act & Assert: al acceder, se carga la entidad y cédula coincide
        var clienteLoaded = found.getCliente();
        assertThat(clienteLoaded).isNotNull();
        assertThat(clienteLoaded.getCedula()).isEqualTo("C-1");
    }

    @Test
    void join_table_rows_are_created_correctly() {
        // Arrange: preparar datos iniciales
        var cli = newCliente("C-2", "Luis", "luis@dominio.com");
        em.persist(cli);

        var p1 = newProducto("Horno", new BigDecimal("100.00"));
        var p2 = newProducto("Mesa", new BigDecimal("50.00"));
        em.persist(p1);
        em.persist(p2);

        var carrito = new CarritoEntity();
        carrito.setIdCliente("C-2");
        carrito.setProductos(new ArrayList<>(List.of(p1, p2)));
        em.persist(carrito);
        em.flush();

        Long carritoId = carrito.getId();
        Long p1Id = p1.getId();
        Long p2Id = p2.getId();
        em.clear();

        // Act: recargar carrito
        var reloaded = em.find(CarritoEntity.class, carritoId);

        // Assert: relación ManyToMany debe contener dos productos
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getProductos())
            .withFailMessage("El carrito debe tener 2 productos")
            .hasSize(2);

        // Assert: tabla puente debe tener exactamente 2 filas para este carrito
        Number count = (Number) em.createNativeQuery(
                        "select count(*) from carrito_productos where carrito_id = ?")
                .setParameter(1, carritoId)
                .getSingleResult();
        assertThat(count.longValue())
            .withFailMessage("Debe haber 2 filas en carrito_productos")
            .isEqualTo(2L);

        // Assert: los IDs de producto enlazados coinciden
        @SuppressWarnings("unchecked")
        var prodIds = (List<Number>) em.createNativeQuery(
                        "select producto_id from carrito_productos where carrito_id = ?")
                .setParameter(1, carritoId)
                .getResultList();

        var expected = List.of(p1Id, p2Id).stream().sorted().toList();
        var actual = prodIds.stream().map(Number::longValue).sorted().toList();
        assertThat(actual)
            .withFailMessage("Los productos enlazados no coinciden")
            .isEqualTo(expected);
    }

    @Test
    void updating_idCliente_switches_association_via_fk_column() {
        // Arrange: dos clientes y carrito inicial
        var c1 = newCliente("C-A", "A", "a@dom.com");
        var c2 = newCliente("C-B", "B", "b@dom.com");
        em.persist(c1);
        em.persist(c2);

        var carrito = new CarritoEntity();
        carrito.setIdCliente("C-A");
        em.persist(carrito);
        em.flush();
        em.clear();

        // Act: cambiar la FK idCliente
        var managed = em.find(CarritoEntity.class, carrito.getId());
        managed.setIdCliente("C-B");
        em.flush();
        em.clear();

        // Assert: la asociación ManyToOne refleja el nuevo cliente
        var reloaded = em.find(CarritoEntity.class, carrito.getId());
        assertThat(reloaded.getIdCliente()).isEqualTo("C-B");
        assertThat(reloaded.getCliente().getCedula()).isEqualTo("C-B");
    }

    @Test
    void mutating_product_list_persists_join_rows() {
        // Arrange: cliente y productos iniciales
        var cli = newCliente("C-Z", "Zoe", "z@dom.com");
        em.persist(cli);

        var p1 = newProducto("Freidora", new BigDecimal("80.00"));
        var p2 = newProducto("Licuadora", new BigDecimal("40.00"));
        var p3 = newProducto("Sartén", new BigDecimal("20.00"));
        em.persist(p1); em.persist(p2); em.persist(p3);

        var carrito = new CarritoEntity();
        carrito.setIdCliente("C-Z");
        // IMPORTANTE: lista MUTABLE para poder hacer add(...)
        carrito.setProductos(new ArrayList(List.of(p1, p2)));
        em.persist(carrito);
        em.flush();

        // Act: añadir un producto más a la lista mutable
        var managed = em.find(CarritoEntity.class, carrito.getId());
        managed.getProductos().add(p3);
        em.flush();

        // Assert: la lista de productos presenta 3 elementos
        var reloaded = em.find(CarritoEntity.class, carrito.getId());
        assertThat(reloaded.getProductos()).hasSize(3);
    }
}
