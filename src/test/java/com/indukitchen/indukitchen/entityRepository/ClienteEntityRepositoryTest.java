package com.indukitchen.indukitchen.entityRepository;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.mapper.ClienteMapperImpl;
import com.indukitchen.indukitchen.persistence.repository.ClienteEntityRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({ClienteEntityRepository.class, ClienteMapperImpl.class})
class ClienteEntityRepositoryIT {

    @Autowired
    EntityManager em;

    @Autowired
    ClienteEntityRepository repository;

    // ========= Helpers =========
    private ClienteEntity newCliente(String cedula, String nombre) {
        var c = new ClienteEntity();
        c.setCedula(cedula);
        c.setNombre(nombre);
        c.setDireccion("Calle 123");
        c.setCorreo(cedula.toLowerCase() + "@dom.com");
        c.setTelefono("3000000000");
        c.setPassword("pwd");       // NOT NULL en tu schema
        c.setLocked(false);         // NOT NULL
        c.setDisabled(false);       // NOT NULL
        em.persist(c);
        return c;
    }

    // ========= Tests =========

    @Test
    @DisplayName("getAll(): devuelve todos los clientes mapeados a DTO")
    void getAll_returns_all_clients() {
        // given
        newCliente("CLI-1", "Ana");
        newCliente("CLI-2", "Luis");
        em.flush();
        em.clear();

        // when
        List<ClienteDto> all = repository.getAll();

        // then
        assertNotNull(all);
        assertTrue(all.size() >= 2);
        assertTrue(all.stream().anyMatch(c -> c.cedula().equals("CLI-1")));
        assertTrue(all.stream().anyMatch(c -> c.cedula().equals("CLI-2")));
    }

    @Test
    @DisplayName("getById(): retorna DTO cuando existe y null cuando no")
    void getById_returns_dto_or_null() {
        // given
        newCliente("CLI-3", "Marta");
        em.flush();
        em.clear();

        // when
        ClienteDto found = repository.getById("CLI-3");
        ClienteDto missing = repository.getById("NO-EXISTE");

        // then
        assertNotNull(found);
        assertEquals("CLI-3", found.cedula());
        assertNull(missing);
    }

    @Test
    @DisplayName("save(): persiste desde DTO y (si el mapper lo retorna) devuelve DTO")
    void save_persists_and_returns_dto() {
        // given
        var dto = new ClienteDto(
                "CLI-4",
                "Pedro",
                "Av 45",
                "pedro@dom.com",
                "3111111111"
        );

        // when
        ClienteDto returned = repository.save(dto);

        // then: siempre verificamos que se persistió en BD
        var persisted = em.find(ClienteEntity.class, "CLI-4");
        assertNotNull(persisted, "El cliente debe existir en BD");
        assertEquals("Pedro", persisted.getNombre());

        // si el mapper devuelve DTO, también lo validamos
        if (returned != null) {
            assertEquals("CLI-4", returned.cedula());
            assertEquals("Pedro", returned.nombre());
        }
    }


    @Test
    @DisplayName("update(): actualiza campos básicos y devuelve DTO")
    void update_updates_fields() {
        // given
        newCliente("CLI-5", "Old Name");
        em.flush();
        em.clear();

        // mantenemos la misma cédula para evitar complicaciones al cambiar PK
        var update = new UpdateClienteDto(
                "CLI-5",
                "Nuevo Nombre",
                "Nueva Dir",
                "nuevo@dom.com",
                "3222222222"
        );

        // when
        ClienteDto updated = repository.update("CLI-5", update);

        // then
        assertNotNull(updated);
        assertEquals("CLI-5", updated.cedula());
        assertEquals("Nuevo Nombre", updated.nombre());
        assertEquals("Nueva Dir", updated.direccion());
        assertEquals("nuevo@dom.com", updated.correo());
        assertEquals("3222222222", updated.telefono());

        // y en BD
        var reloaded = em.find(ClienteEntity.class, "CLI-5");
        assertEquals("Nuevo Nombre", reloaded.getNombre());
        assertEquals("Nueva Dir", reloaded.getDireccion());
        assertEquals("nuevo@dom.com", reloaded.getCorreo());
        assertEquals("3222222222", reloaded.getTelefono());
    }

    @Test
    @DisplayName("delete(): elimina por cédula sin excepciones")
    void delete_removes_entity() {
        // given
        newCliente("CLI-6", "Borrar");
        em.flush();
        em.clear();

        // when
        repository.delete("CLI-6");
        em.flush();
        em.clear();

        // then
        assertNull(em.find(ClienteEntity.class, "CLI-6"));
    }
}
