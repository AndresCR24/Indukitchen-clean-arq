package com.indukitchen.indukitchen.entity;

import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ClienteEntityTest {

    @Autowired
    EntityManager em;

    private ClienteEntity newCliente(String cedula, String nombre, String correo) {
        var c = new ClienteEntity();
        c.setCedula(cedula);
        c.setNombre(nombre);
        c.setCorreo(correo);
        // Campos NOT NULL en la entidad
        c.setDireccion("Calle 123");
        c.setTelefono("3001234567"); // <= 17 chars

        return c;
    }

    @Test
    void persist_required_fields_and_roundtrip() {
        // given
        var c = newCliente("CLI-1", "Ana", "ana@dom.com");

        // when
        em.persist(c);
        em.flush();
        em.clear();

        // then
        var found = em.find(ClienteEntity.class, "CLI-1");
        assertNotNull(found);
        assertEquals("Ana", found.getNombre());
        assertEquals("ana@dom.com", found.getCorreo());
        assertEquals("Calle 123", found.getDireccion());
        assertEquals("3001234567", found.getTelefono());


        assertNotNull(found.getCarritos());
        assertTrue(found.getCarritos().isEmpty());
    }

    @Test
    void one_to_many_is_lazy_and_loads_on_access() {
        // given: cliente + dos carritos que apuntan por FK
        var cli = newCliente("CLI-2", "Luis", "luis@dom.com");
        em.persist(cli);

        var cart1 = new CarritoEntity();
        cart1.setIdCliente("CLI-2");
        em.persist(cart1);

        var cart2 = new CarritoEntity();
        cart2.setIdCliente("CLI-2");
        em.persist(cart2);

        em.flush();
        em.clear();

        // when
        var reload = em.find(ClienteEntity.class, "CLI-2");
        var util = Persistence.getPersistenceUtil();

        // then: LAZY inicialmente
        assertFalse(util.isLoaded(reload, "carritos"), "carritos debe ser LAZY inicialmente");

        // al acceder, debe cargar y traer 2
        assertEquals(2, reload.getCarritos().size());
    }

    @Test
    void duplicate_primary_key_throws_exception_on_flush() {
        // given
        var a = newCliente("CLI-3", "Zoe", "z@dom.com");
        em.persist(a);
        em.flush();   // escribe A
        em.clear();   // A ya no queda gestionado en el contexto

        // when: otro con misma PK
        var b = newCliente("CLI-3", "Zoe 2", "z2@dom.com");
        em.persist(b);

        // then: ahora la colisión ocurre en la BD al hacer flush
        assertThrows(PersistenceException.class, () -> em.flush());
    }


    @Test
    void updating_fields_persists_changes() {
        // given
        var c = newCliente("CLI-4", "Mia", "mia@dom.com");
        em.persist(c);
        em.flush();
        em.clear();

        // when
        var managed = em.find(ClienteEntity.class, "CLI-4");
        managed.setNombre("Mia Updated");
        managed.setCorreo("mia+u@dom.com");
        managed.setDireccion("Av 45 #10-20");
        managed.setTelefono("3110000000");

        em.flush();
        em.clear();

        // then
        var r = em.find(ClienteEntity.class, "CLI-4");
        assertEquals("Mia Updated", r.getNombre());
        assertEquals("mia+u@dom.com", r.getCorreo());
        assertEquals("Av 45 #10-20", r.getDireccion());
        assertEquals("3110000000", r.getTelefono());


    }
}



