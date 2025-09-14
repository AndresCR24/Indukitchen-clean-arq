package com.indukitchen.indukitchen.entity;

import com.indukitchen.indukitchen.persistence.entity.MetodoPagoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MetodoPagoEntityTest {

    @Autowired
    EntityManager em;

    private MetodoPagoEntity nuevo(Boolean efectivo, Boolean tarjeta) {
        var mp = new MetodoPagoEntity();
        mp.setEfectivo(efectivo);
        mp.setTarjeta(tarjeta);
        return mp;
    }

    @Test
    void persist_and_load_with_null_flags_generates_id() {
        // given: ambos flags nulos (columnas son NULLABLE)
        var mp = nuevo(null, null);

        // when
        em.persist(mp);
        em.flush();
        em.clear();

        // then
        assertTrue(mp.getId() > 0, "El ID debe ser generado (IDENTITY)");
        var found = em.find(MetodoPagoEntity.class, mp.getId());
        assertNotNull(found);
        assertNull(found.getEfectivo());
        assertNull(found.getTarjeta());
    }

    @Test
    void persist_with_values_and_update_them() {
        // given
        var mp = nuevo(Boolean.TRUE, Boolean.FALSE);
        em.persist(mp);
        em.flush();
        em.clear();

        // when: verificamos valores iniciales
        var loaded = em.find(MetodoPagoEntity.class, mp.getId());
        assertNotNull(loaded);
        assertEquals(Boolean.TRUE, loaded.getEfectivo());
        assertEquals(Boolean.FALSE, loaded.getTarjeta());

        // and: actualizamos a TRUE/TRUE
        loaded.setTarjeta(Boolean.TRUE);
        em.flush();
        em.clear();

        // then
        var reloaded = em.find(MetodoPagoEntity.class, mp.getId());
        assertEquals(Boolean.TRUE, reloaded.getEfectivo());
        assertEquals(Boolean.TRUE, reloaded.getTarjeta());
    }

    @Test
    void multiple_rows_are_independent_and_have_distinct_ids() {
        // given
        var mp1 = nuevo(Boolean.TRUE, null);
        var mp2 = nuevo(null, Boolean.TRUE);

        // when
        em.persist(mp1);
        em.persist(mp2);
        em.flush();
        em.clear();

        // then
        assertTrue(mp1.getId() > 0);
        assertTrue(mp2.getId() > 0);
        assertNotEquals(mp1.getId(), mp2.getId(), "Cada fila debe tener un ID distinto");

        var r1 = em.find(MetodoPagoEntity.class, mp1.getId());
        var r2 = em.find(MetodoPagoEntity.class, mp2.getId());
        assertEquals(Boolean.TRUE, r1.getEfectivo());
        assertNull(r1.getTarjeta());
        assertNull(r2.getEfectivo());
        assertEquals(Boolean.TRUE, r2.getTarjeta());
    }

    @Test
    void setting_flags_back_to_null_is_persisted() {
        // given
        var mp = nuevo(Boolean.TRUE, Boolean.TRUE);
        em.persist(mp);
        em.flush();

        // when
        var managed = em.find(MetodoPagoEntity.class, mp.getId());
        managed.setEfectivo(null);
        managed.setTarjeta(null);
        em.flush();
        em.clear();

        // then
        var reloaded = em.find(MetodoPagoEntity.class, mp.getId());
        assertNull(reloaded.getEfectivo());
        assertNull(reloaded.getTarjeta());
    }
}
