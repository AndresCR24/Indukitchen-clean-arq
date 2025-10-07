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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
        c.setPassword("pwd");
        c.setLocked(false);
        c.setDisabled(false);
        return c;
    }

    @Test
    void persist_required_fields_and_roundtrip() {
        // Arrange
        var c = newCliente("CLI-1", "Ana", "ana@dom.com");

        // Act
        em.persist(c);
        em.flush();
        em.clear();

        // Assert
        var found = em.find(ClienteEntity.class, "CLI-1");
        assertThat(found).isNotNull();
        assertThat(found.getNombre()).isEqualTo("Ana");
        assertThat(found.getCorreo()).isEqualTo("ana@dom.com");
        assertThat(found.getDireccion()).isEqualTo("Calle 123");
        assertThat(found.getTelefono()).isEqualTo("3001234567");
        assertThat(found.getPassword()).isEqualTo("pwd");
        assertThat(found.getLocked()).isFalse();
        assertThat(found.getDisabled()).isFalse();
        assertThat(found.getCarritos()).isNotNull().isEmpty();
    }

    @Test
    void one_to_many_is_lazy_and_loads_on_access() {
        // Arrange: cliente + dos carritos que apuntan por FK
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

        // Act
        var reload = em.find(ClienteEntity.class, "CLI-2");
        var util = Persistence.getPersistenceUtil();

        // Assert: LAZY inicialmente y carga al acceder
        assertThat(util.isLoaded(reload, "carritos")).as("carritos debe ser LAZY inicialmente").isFalse();
        assertThat(reload.getCarritos()).hasSize(2);
    }

    @Test
    void duplicate_primary_key_throws_exception_on_flush() {
        // Arrange
        var a = newCliente("CLI-3", "Zoe", "z@dom.com");
        em.persist(a);
        em.flush();   // escribe A
        em.clear();   // A ya no queda gestionado en el contexto

        var b = newCliente("CLI-3", "Zoe 2", "z2@dom.com");
        em.persist(b);

        // Act & Assert
        assertThatThrownBy(() -> em.flush())
                .isInstanceOf(PersistenceException.class);
    }


    @Test
    void updating_fields_persists_changes() {
        // Arrange
        var c = newCliente("CLI-4", "Mia", "mia@dom.com");
        em.persist(c);
        em.flush();
        em.clear();

        // Act
        var managed = em.find(ClienteEntity.class, "CLI-4");
        managed.setNombre("Mia Updated");
        managed.setCorreo("mia+u@dom.com");
        managed.setDireccion("Av 45 #10-20");
        managed.setTelefono("3110000000");
        managed.setPassword("pwd2");
        managed.setLocked(true);
        managed.setDisabled(true);
        em.flush();
        em.clear();

        // Assert
        var r = em.find(ClienteEntity.class, "CLI-4");
        assertThat(r.getNombre()).isEqualTo("Mia Updated");
        assertThat(r.getCorreo()).isEqualTo("mia+u@dom.com");
        assertThat(r.getDireccion()).isEqualTo("Av 45 #10-20");
        assertThat(r.getTelefono()).isEqualTo("3110000000");
        assertThat(r.getPassword()).isEqualTo("pwd2");
        assertThat(r.getLocked()).isTrue();
        assertThat(r.getDisabled()).isTrue();
    }

    @Test
    void setCarritos_assigns_collection_and_roundtrips() {
        // Arrange: cliente y dos carritos asociados por FK
        var cli = newCliente("CLI-7", "Nora", "n@dom.com");
        em.persist(cli);

        var cart1 = new CarritoEntity();
        cart1.setIdCliente("CLI-7");
        em.persist(cart1);

        var cart2 = new CarritoEntity();
        cart2.setIdCliente("CLI-7");
        em.persist(cart2);

        em.flush();
        em.clear();

        // Obtenemos el cliente y los carritos desde BD
        var reload = em.find(ClienteEntity.class, "CLI-7");
        var carts = em.createQuery(
                        "select c from CarritoEntity c where c.idCliente = :id", CarritoEntity.class)
                .setParameter("id", "CLI-7")
                .getResultList();

        // Act: asignamos explícitamente la colección con el setter
        reload.setCarritos(carts);
        em.flush();
        em.clear();

        // Assert: al volver a acceder, la colección refleja lo asignado
        var after = em.find(ClienteEntity.class, "CLI-7");
        assertThat(after.getCarritos())
                .as("setCarritos debe reflejarse al acceder a la colección")
                .hasSize(2)
                .allSatisfy(c -> assertThat(c.getIdCliente()).isEqualTo("CLI-7"));
    }

}