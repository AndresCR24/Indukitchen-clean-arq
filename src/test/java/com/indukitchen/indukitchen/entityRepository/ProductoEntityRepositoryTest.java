package com.indukitchen.indukitchen.entityRepository;

import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import com.indukitchen.indukitchen.persistence.mapper.ProductoMapper;
import com.indukitchen.indukitchen.persistence.repository.ProductoEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({ProductoEntityRepositoryIT.TestConfig.class, ProductoEntityRepository.class})
class ProductoEntityRepositoryIT {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ProductoEntityRepository repository;

    // ------------ helpers ------------
    private ProductoEntity newProducto(String nombre, String descripcion, BigDecimal precio,
                                       Integer existencia, Double peso, String imagen) {
        var p = new ProductoEntity();
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setExistencia(existencia);
        p.setPeso(peso);
        p.setImagen(imagen);
        return p;
    }

    // ------------ tests ------------
    @Test
    @DisplayName("getAll(): retorna todos los registros mapeados a DTO")
    void getAll_returns_all_mapped_dtos() {
        var p1 = newProducto("Horno", "Horno eléctrico", new BigDecimal("120.50"), 10, 8.0, "horno.jpg");
        var p2 = newProducto("Mesa", "Mesa de cocina", new BigDecimal("80.00"), 5, 12.5, "mesa.jpg");
        em.persist(p1);
        em.persist(p2);
        em.flush();
        em.clear();

        List<ProductoDto> all = repository.getAll();

        assertNotNull(all);
        assertTrue(all.size() >= 2);
        var nombres = all.stream().map(ProductoDto::nombre).toList();
        assertTrue(nombres.contains("Horno"));
        assertTrue(nombres.contains("Mesa"));
    }

    @Test
    @DisplayName("getById(): retorna DTO mapeado cuando existe")
    void getById_returns_mapped_dto_when_exists() {
        var p = newProducto("Batidora", "Batidora 300W", new BigDecimal("49.99"), 20, 1.8, "bati.jpg");
        em.persist(p);
        em.flush();
        em.clear();

        ProductoDto dto = repository.getById(p.getId());

        assertNotNull(dto);
        assertEquals(p.getId(), dto.id());
        assertEquals("Batidora", dto.nombre());
        assertEquals(new BigDecimal("49.99"), dto.precio());
        assertEquals(20, dto.existencia());
        assertEquals(1.8, dto.peso());
        assertEquals("bati.jpg", dto.imagen());
    }

    @Test
    @DisplayName("getById(): retorna null cuando no existe")
    void getById_returns_null_when_absent() {
        assertNull(repository.getById(9_999_999L));
    }

    @Test
    @DisplayName("save(): persiste desde DTO y retorna DTO con id generado")
    void save_persists_from_dto_and_returns_generated_id() {
        var dto = new ProductoDto(
                0L,
                "Sartén",
                "Sartén antiadherente",
                new BigDecimal("35.25"),
                15,
                0.9,
                "sarten.png"
        );

        ProductoDto saved = repository.save(dto);

        assertNotNull(saved);
        assertTrue(saved.id() > 0);
        assertEquals("Sartén", saved.nombre());
        assertEquals(new BigDecimal("35.25"), saved.precio());
        assertEquals(15, saved.existencia());
        assertEquals(0.9, saved.peso());
        assertEquals("sarten.png", saved.imagen());

        var found = em.find(ProductoEntity.class, saved.id());
        assertNotNull(found);
        assertEquals("Sartén", found.getNombre());
        assertEquals(new BigDecimal("35.25"), found.getPrecio());
        assertEquals(15, found.getExistencia());
        assertEquals(0.9, found.getPeso());
        assertEquals("sarten.png", found.getImagen());
    }

    @Test
    @DisplayName("delete(): elimina por id")
    void delete_removes_row() {
        var p = newProducto("Cafetera", "Cafetera 1.2L", new BigDecimal("89.00"), 7, 2.1, "cafetera.jpg");
        em.persist(p);
        em.flush();

        long id = p.getId();
        assertNotNull(em.find(ProductoEntity.class, id));

        repository.delete(id);
        em.flush();

        assertNull(em.find(ProductoEntity.class, id));
    }

    // ------------ configuración de test (mapper simple) ------------
    @TestConfiguration
    static class TestConfig {
        @Bean
        ProductoMapper productoMapper() { return new SimpleProductoMapper(); }
    }

    static class SimpleProductoMapper implements ProductoMapper {
        @Override
        public ProductoDto toDto(ProductoEntity e) {
            if (e == null) return null;
            return new ProductoDto(
                    e.getId(),
                    e.getNombre(),
                    e.getDescripcion(),
                    e.getPrecio(),
                    e.getExistencia(),
                    e.getPeso(),
                    e.getImagen()
            );
        }

        @Override
        public List<ProductoDto> toDto(Iterable<ProductoEntity> entities) {
            if (entities == null) return null;
            List<ProductoDto> list = new ArrayList<>();
            for (ProductoEntity e : entities) list.add(toDto(e));
            return list;
        }

        @Override
        public ProductoEntity toEntity(ProductoDto d) {
            if (d == null) return null;
            var e = new ProductoEntity();
            e.setId(d.id()); // si es 0L, H2/IDENTITY lo ignora y genera
            e.setNombre(d.nombre());
            e.setDescripcion(d.descripcion());
            e.setPrecio(d.precio());
            e.setExistencia(d.existencia());
            e.setPeso(d.peso());
            e.setImagen(d.imagen());
            return e;
        }

    }
}
