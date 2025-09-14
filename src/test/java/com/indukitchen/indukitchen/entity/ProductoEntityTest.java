package com.indukitchen.indukitchen.entity;

import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProductoEntityTest {

    @Autowired
    EntityManager em;

    // ========= helpers =========
    private ProductoEntity nuevoProducto(String nombre, BigDecimal precio) {
        var p = new ProductoEntity();
        p.setNombre(nombre);
        p.setPrecio(precio);
        return p;
    }

    private ClienteEntity nuevoClienteValido(String cedula) {
        var c = new ClienteEntity();
        c.setCedula(cedula);
        c.setNombre("Nombre");
        c.setDireccion("Dir corta 123");
        c.setTelefono("3001234567");
        c.setPassword("pwd");
        c.setLocked(false);
        c.setDisabled(false);
        // correo es nullable
        return c;
    }

    // ========= tests =========

    @Test
    void persist_and_load_all_fields_roundtrip() {
        // given
        var p = nuevoProducto("Sartén", new BigDecimal("19.90"));
        p.setDescripcion("Anti adherente 24cm");
        p.setExistencia(15);
        p.setPeso(0.75);
        p.setImagen("sarten.png");

        // when
        em.persist(p);
        em.flush();
        long id = p.getId();
        em.clear();

        // then
        assertTrue(id > 0, "Debe generarse ID (IDENTITY)");
        var found = em.find(ProductoEntity.class, id);
        assertNotNull(found);
        assertEquals("Sartén", found.getNombre());
        assertEquals(new BigDecimal("19.90"), found.getPrecio());
        assertEquals("Anti adherente 24cm", found.getDescripcion());
        assertEquals(15, found.getExistencia());
        assertEquals(0.75, found.getPeso());
        assertEquals("sarten.png", found.getImagen());
    }

    @Test
    void nullables_are_allowed_and_read_back_as_null() {
        // given: solo nombre y precio; resto NULL
        var p = nuevoProducto("Olla", new BigDecimal("45.00"));

        // when
        em.persist(p);
        em.flush();
        long id = p.getId();
        em.clear();

        // then
        var found = em.find(ProductoEntity.class, id);
        assertNotNull(found);
        assertEquals("Olla", found.getNombre());
        assertEquals(new BigDecimal("45.00"), found.getPrecio());
        assertNull(found.getDescripcion());
        assertNull(found.getExistencia());
        assertNull(found.getPeso());
        assertNull(found.getImagen());
    }

    @Test
    void updating_fields_is_persisted() {
        // given
        var p = nuevoProducto("Licuadora", new BigDecimal("120.00"));
        p.setExistencia(3);
        em.persist(p);
        em.flush();

        // when
        var managed = em.find(ProductoEntity.class, p.getId());
        managed.setNombre("Licuadora Pro");
        managed.setPrecio(new BigDecimal("139.99"));
        managed.setExistencia(5);
        managed.setDescripcion("Vaso de vidrio 1.5L");
        em.flush();
        em.clear();

        // then
        var reloaded = em.find(ProductoEntity.class, p.getId());
        assertEquals("Licuadora Pro", reloaded.getNombre());
        assertEquals(new BigDecimal("139.99"), reloaded.getPrecio());
        assertEquals(5, reloaded.getExistencia());
        assertEquals("Vaso de vidrio 1.5L", reloaded.getDescripcion());
    }

    @Test
    void inverse_many_to_many_populates_when_owning_side_updates() {
        // given: un producto y un carrito (para carrito necesitamos cliente válido)
        var prod = nuevoProducto("Plancha", new BigDecimal("85.50"));
        em.persist(prod);

        var cli = nuevoClienteValido("CLI-P1");
        em.persist(cli);

        var carrito = new CarritoEntity();
        carrito.setIdCliente("CLI-P1");
        // el lado dueño es CarritoEntity.productos, así que se setea ahí:
        var lista = new ArrayList<ProductoEntity>();
        lista.add(prod);
        carrito.setProductos(lista);
        em.persist(carrito);

        em.flush();
        long prodId = prod.getId();
        em.clear();

        // when: cargamos el producto y revisamos el lado inverso
        var foundProd = em.find(ProductoEntity.class, prodId);
        assertNotNull(foundProd);
        var carritosQueLoContienen = foundProd.getCarritos();

        // then
        assertNotNull(carritosQueLoContienen);
        assertEquals(1, carritosQueLoContienen.size(), "Debe existir una fila en la join-table para el producto");
        assertEquals("CLI-P1", carritosQueLoContienen.get(0).getIdCliente());
    }
}
