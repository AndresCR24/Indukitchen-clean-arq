package com.indukitchen.indukitchen.persistence.crud;

import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CrudFacturaEntity extends JpaRepository<FacturaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {
            "carritoFactura",
            "carritoFactura.cliente",
            "carritoFactura.productos"  // <- MUY IMPORTANTE
    })
    Optional<FacturaEntity> findById(Long id);
}
