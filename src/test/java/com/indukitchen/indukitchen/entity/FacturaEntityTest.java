package com.indukitchen.indukitchen.entity;

import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import com.indukitchen.indukitchen.persistence.entity.MetodoPagoEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FacturaEntityTest {

    @Autowired
    EntityManager em;

    // ===== Helpers =====

    private ClienteEntity newCliente(String cedula, String nombre, String correo) {
        var c = new ClienteEntity();
        c.setCedula(cedula);
        c.setNombre(nombre);
        c.setDireccion("Calle 1 #2-3");
        c.setCorreo(correo);
        c.setTelefono("3000000000");

        return c;
    }

    private CarritoEntity newCarrito(String cedulaCliente) {
        var ca = new CarritoEntity();
        ca.setIdCliente(cedulaCliente);
        return ca;
    }

    private MetodoPagoEntity newMetodoPago(boolean efectivo, boolean tarjeta) {
        var mp = new MetodoPagoEntity();
        mp.setEfectivo(efectivo);
        mp.setTarjeta(tarjeta);
        return mp;
    }

    // ===== Tests =====

    @Test
    void persist_and_eager_load_relations() {
        // given: cliente + carrito + metodo de pago existentes
        var cli = newCliente("CLI-1", "Ana", "ana@dom.com");
        em.persist(cli);

        var carrito = newCarrito("CLI-1");
        em.persist(carrito);

        var mp = newMetodoPago(true, false);
        em.persist(mp);

        var f = new FacturaEntity();
        f.setIdCarrito(carrito.getId());
        f.setIdMetodoPago((int) mp.getId()); // columna int en 'facturas'
        em.persist(f);
        em.flush();
        em.clear();

        // when
        var found = em.find(FacturaEntity.class, f.getId());

        // then: datos básicos
        assertNotNull(found);
        assertEquals(carrito.getId(), found.getIdCarrito());
        assertEquals((int) mp.getId(), found.getIdMetodoPago());

        // y relaciones @OneToOne EAGER
        var util = Persistence.getPersistenceUtil();
        assertTrue(util.isLoaded(found, "carritoFactura"), "carritoFactura debería cargarse EAGER");
        assertTrue(util.isLoaded(found, "metodoPagoFactura"), "metodoPagoFactura debería cargarse EAGER");

        assertNotNull(found.getCarritoFactura());
        assertEquals(carrito.getId(), found.getCarritoFactura().getId());

        assertNotNull(found.getMetodoPagoFactura());
        assertEquals(mp.getId(), found.getMetodoPagoFactura().getId());
    }

    @Test
    void updating_idCarrito_switches_association() {
        // given: cliente con dos carritos
        var cli = newCliente("CLI-2", "Luis", "luis@dom.com");
        em.persist(cli);

        var c1 = newCarrito("CLI-2");
        var c2 = newCarrito("CLI-2");
        em.persist(c1);
        em.persist(c2);

        var f = new FacturaEntity();
        f.setIdCarrito(c1.getId());
        em.persist(f);
        em.flush();
        em.clear();

        // when: cambiamos la FK
        var managed = em.find(FacturaEntity.class, f.getId());
        assertNotNull(managed);
        managed.setIdCarrito(c2.getId());
        em.flush();
        em.clear();

        // then: la asociación ahora apunta al carrito 2
        var reloaded = em.find(FacturaEntity.class, f.getId());
        assertEquals(c2.getId(), reloaded.getIdCarrito());
        assertNotNull(reloaded.getCarritoFactura());
        assertEquals(c2.getId(), reloaded.getCarritoFactura().getId());
    }

    @Test
    void metodoPago_nullable_then_set_and_relation_resolves() {
        // given: cliente + carrito
        var cli = newCliente("CLI-3", "Zoe", "zoe@dom.com");
        em.persist(cli);
        var carrito = newCarrito("CLI-3");
        em.persist(carrito);

        // y una factura sin método de pago
        var f = new FacturaEntity();
        f.setIdCarrito(carrito.getId());
        f.setIdMetodoPago(null);
        em.persist(f);
        em.flush();
        em.clear();

        // then: relación nula inicialmente
        var found = em.find(FacturaEntity.class, f.getId());
        assertNotNull(found);
        assertNull(found.getIdMetodoPago());
        assertNull(found.getMetodoPagoFactura());

        // when: creamos método de pago y seteamos FK en factura
        var mp = newMetodoPago(false, true);
        em.persist(mp);
        em.flush();

        var managed = em.find(FacturaEntity.class, f.getId());
        managed.setIdMetodoPago(Objects.requireNonNull(mp.getId()).intValue());
        em.flush();
        em.clear();

        // then: relación resuelta
        var reloaded = em.find(FacturaEntity.class, f.getId());
        managed.setIdMetodoPago((int) mp.getId());
        assertNotNull(reloaded.getMetodoPagoFactura());
        assertEquals(mp.getId(), reloaded.getMetodoPagoFactura().getId());
    }

    @Test
    void idCarrito_fk_must_exist_and_is_validated_on_persist() {
        // No seteamos idCarrito -> queda en 0 (long), FK inválida
        var factura = new FacturaEntity();

        assertThrows(org.hibernate.exception.ConstraintViolationException.class,
                () -> em.persist(factura));
    }


    @Test
    void idCarrito_not_null_constraint_is_enforced() {
        var f = new FacturaEntity(); // idCarrito = null
        assertThrows(org.hibernate.exception.ConstraintViolationException.class,
                () -> em.persist(f)); // con IDENTITY insertará en persist()
    }


    @Test
    void assigning_readonly_association_does_not_write_fk_column() {
        // given
        var cli = newCliente("CLI-4", "Eva", "eva@dom.com");
        em.persist(cli);
        var carrito = newCarrito("CLI-4");
        em.persist(carrito);

        var mp = newMetodoPago(true, true);
        em.persist(mp);

        var f = new FacturaEntity();
        f.setIdCarrito(carrito.getId());
        // No seteamos idMetodoPago (null), pero ASIGNAMOS la asociación:
        f.setMetodoPagoFactura(mp); // mapeo es insertable=false, updatable=false
        em.persist(f);
        em.flush();
        em.clear();

        // when
        var found = em.find(FacturaEntity.class, f.getId());

        // then: la FK sigue siendo null; la relación no se materializa
        assertNull(found.getIdMetodoPago(), "idMetodoPago debe seguir null al no ser escribible por la asociación");
        assertNull(found.getMetodoPagoFactura(), "metodoPagoFactura debe ser null al no existir FK");
    }
}

