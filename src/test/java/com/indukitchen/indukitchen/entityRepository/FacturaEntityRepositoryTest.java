package com.indukitchen.indukitchen.entityRepository;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import com.indukitchen.indukitchen.persistence.mapper.FacturaMapper;
import com.indukitchen.indukitchen.persistence.repository.FacturaEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({FacturaEntityRepositoryIT.TestConfig.class, FacturaEntityRepository.class})
class FacturaEntityRepositoryIT {

    @PersistenceContext
    EntityManager em;

    // Inyectado desde el contexto (gracias al @Import del repo)
    private final FacturaEntityRepository repository;

    @Autowired
    FacturaEntityRepositoryIT(FacturaEntityRepository repository) {
        this.repository = repository;
    }

    // ----------------- Helpers -----------------

    private ClienteEntity newCliente(String cedula, String nombre, String correo) {
        var c = new ClienteEntity();
        c.setCedula(cedula);
        c.setNombre(nombre);
        c.setDireccion("Calle 123");
        c.setCorreo(correo);
        c.setTelefono("3000000000");
        c.setPassword("pwd");
        c.setLocked(false);
        c.setDisabled(false);
        return c;
    }

    private CarritoEntity newCarrito(String idCliente) {
        var carrito = new CarritoEntity();
        carrito.setIdCliente(idCliente);
        return carrito;
    }

    // ----------------- Tests -----------------

    @Test
    @DisplayName("getAll(): retorna todos los registros mapeados a DTO")
    void getAll_returns_all_mapped_dtos() {
        var cli = newCliente("CLI-1", "Ana", "ana@dom.com");
        em.persist(cli);
        var car1 = newCarrito("CLI-1");
        var car2 = newCarrito("CLI-1");
        em.persist(car1);
        em.persist(car2);

        var f1 = new FacturaEntity();
        f1.setIdCarrito(car1.getId());
        em.persist(f1);

        var f2 = new FacturaEntity();
        f2.setIdCarrito(car2.getId());
        em.persist(f2);

        em.flush();
        em.clear();

        List<FacturaDto> all = repository.getAll();

        assertNotNull(all);
        assertTrue(all.size() >= 2);
        var carritoIds = all.stream().map(FacturaDto::idCarrito).toList();
        assertTrue(carritoIds.contains(car1.getId()));
        assertTrue(carritoIds.contains(car2.getId()));
    }

    @Test
    @DisplayName("getById(): retorna DTO mapeado cuando existe")
    void getById_returns_mapped_dto() {
        var cli = newCliente("CLI-2", "Sofi", "sofi@dom.com");
        em.persist(cli);
        var car = newCarrito("CLI-2");
        em.persist(car);

        var f = new FacturaEntity();
        f.setIdCarrito(car.getId());
        em.persist(f);

        em.flush();
        em.clear();

        FacturaDto dto = repository.getById(f.getId());

        assertNotNull(dto);
        assertEquals(f.getId(), dto.id());
        assertEquals(car.getId(), dto.idCarrito());
        assertNull(dto.idMetodoPago());
    }

    @Test
    @DisplayName("getById(): retorna null cuando no existe")
    void getById_returns_null_when_absent() {
        assertNull(repository.getById(987_654_321L));
    }

    @Test
    @DisplayName("save(): persiste respetando FK y devuelve DTO con id generado")
    void save_persists_and_returns_dto() {
        var cli = newCliente("CLI-3", "Pedro", "pedro@dom.com");
        em.persist(cli);
        var car = newCarrito("CLI-3");
        em.persist(car);
        em.flush();

        var dto = new FacturaDto(0L, car.getId(), null);

        FacturaDto saved = repository.save(dto);

        assertNotNull(saved);
        assertTrue(saved.id() > 0);
        assertEquals(car.getId(), saved.idCarrito());

        var found = em.find(FacturaEntity.class, saved.id());
        assertNotNull(found);
        assertEquals(car.getId(), found.getIdCarrito());
    }

    @Test
    @DisplayName("delete(): elimina por id")
    void delete_removes_row() {
        var cli = newCliente("CLI-4", "Mia", "mia@dom.com");
        em.persist(cli);
        var car = newCarrito("CLI-4");
        em.persist(car);

        var f = new FacturaEntity();
        f.setIdCarrito(car.getId());
        em.persist(f);
        em.flush();

        long id = f.getId();

        repository.delete(id);
        em.flush();

        assertNull(em.find(FacturaEntity.class, id));
    }

    @Test
    @DisplayName("save(): falla por integridad referencial si idCarrito no existe")
    void save_fails_when_invalid_fk() {
        var invalidDto = new FacturaDto(0L, 0L, null); // 0 no existe en carritos

        assertThrows(Exception.class, () -> {
            repository.save(invalidDto);
            em.flush(); // fuerza la violación FK en H2
        });
    }

    // ----------------- TestConfig con mapper “manual” -----------------

    static class TestConfig {
        @Bean
        FacturaMapper facturaMapper() {
            return new SimpleFacturaMapper();
        }
    }

    /**
     * Implementación mínima del FacturaMapper para pruebas.
     * Nota: implementa las firmas con Iterable para coincidir con la interfaz real.
     */
    static class SimpleFacturaMapper implements FacturaMapper {
        @Override
        public FacturaDto toDto(FacturaEntity entity) {
            if (entity == null) return null;
            return new FacturaDto(entity.getId(), entity.getIdCarrito(), entity.getIdMetodoPago());
        }

        @Override
        public List<FacturaDto> toDto(Iterable<FacturaEntity> entities) {
            if (entities == null) return null;
            List<FacturaDto> list = new ArrayList<>();
            for (FacturaEntity e : entities) {
                list.add(toDto(e));
            }
            return list;
        }

        @Override
        public FacturaEntity toEntity(FacturaDto dto) {
            if (dto == null) return null;
            var e = new FacturaEntity();
            // Si id = 0, Hibernate genera el identity
            e.setId(dto.id());
            e.setIdCarrito(dto.idCarrito());
            e.setIdMetodoPago(dto.idMetodoPago());
            return e;
        }

    }
}



