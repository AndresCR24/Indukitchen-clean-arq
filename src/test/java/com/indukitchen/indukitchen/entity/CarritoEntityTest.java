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

import static org.junit.jupiter.api.Assertions.*;

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
        // given: cliente y productos existentes
        var cli = newCliente("C-1", "Ana", "ana@dominio.com");
        em.persist(cli);

        var p1 = newProducto("Plancha", new BigDecimal("12.50"));
        var p2 = newProducto("Campana", new BigDecimal("35.00"));
        em.persist(p1);
        em.persist(p2);

        // and: carrito con idCliente + productos
        var carrito = new CarritoEntity();
        carrito.setIdCliente("C-1");
        carrito.setProductos(List.of(p1, p2));
        em.persist(carrito);
        em.flush();
        em.clear();

        // when: recargamos el carrito
        var found = em.find(CarritoEntity.class, carrito.getId());
        assertNotNull(found);
        assertEquals("C-1", found.getIdCliente());
        assertEquals(2, found.getProductos().size());

        // then: @ManyToOne LAZY (sin forzar carga)
        var util = Persistence.getPersistenceUtil();
        assertFalse(util.isLoaded(found, "cliente"), "Cliente debería ser LAZY inicialmente");

        // al acceder, debería cargar y coincidir la cédula
        var clienteLoaded = found.getCliente();
        assertNotNull(clienteLoaded);
        assertEquals("C-1", clienteLoaded.getCedula());
    }

    @Test
    void join_table_rows_are_created_correctly() {
        // given
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

        // when
        var reloaded = em.find(CarritoEntity.class, carritoId);

        // then: relación ManyToMany poblada
        assertNotNull(reloaded);
        assertEquals(2, reloaded.getProductos().size(), "El carrito debe tener 2 productos");

        // y la tabla puente tiene exactamente 2 filas para este carrito
        Number count = (Number) em.createNativeQuery(
                        "select count(*) from carrito_productos where carrito_id = ?")
                .setParameter(1, carritoId)
                .getSingleResult();
        assertEquals(2L, count.longValue(), "Debe haber 2 filas en carrito_productos");

        // y los IDs de producto coinciden exactamente
        @SuppressWarnings("unchecked")
        var prodIds = (List<Number>) em.createNativeQuery(
                        "select producto_id from carrito_productos where carrito_id = ?")
                .setParameter(1, carritoId)
                .getResultList();

        var expected = List.of(p1Id, p2Id).stream().sorted().toList();
        var actual = prodIds.stream().map(Number::longValue).sorted().toList();
        assertEquals(expected, actual, "Los productos enlazados no coinciden");
    }

    @Test
    void updating_idCliente_switches_association_via_fk_column() {
        // given: dos clientes
        var c1 = newCliente("C-A", "A", "a@dom.com");
        var c2 = newCliente("C-B", "B", "b@dom.com");
        em.persist(c1);
        em.persist(c2);

        var carrito = new CarritoEntity();
        carrito.setIdCliente("C-A");
        em.persist(carrito);
        em.flush();
        em.clear();

        // when: cambiamos la FK (idCliente)
        var managed = em.find(CarritoEntity.class, carrito.getId());
        managed.setIdCliente("C-B");
        em.flush();
        em.clear();

        // then: la asociación ManyToOne refleja el nuevo cliente
        var reloaded = em.find(CarritoEntity.class, carrito.getId());
        assertEquals("C-B", reloaded.getIdCliente());
        assertEquals("C-B", reloaded.getCliente().getCedula());
    }

    @Test
    void mutating_product_list_persists_join_rows() {
        // given
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

        // when: añadimos un producto más
        var managed = em.find(CarritoEntity.class, carrito.getId());
        managed.getProductos().add(p3);
        em.flush();

        // then
        var reloaded = em.find(CarritoEntity.class, carrito.getId());
        assertEquals(3, reloaded.getProductos().size());
    }
}

