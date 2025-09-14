package com.indukitchen.indukitchen.persistence.crud;

import com.indukitchen.indukitchen.persistence.entity.CarritoEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CrudCarritoEntity extends CrudRepository<CarritoEntity, Long> {
    //ClienteEntity findFirstByNombre(String nombre);
    @Override
    @EntityGraph(attributePaths = "productos")
    List<CarritoEntity> findAll();

    @Override
    @EntityGraph(attributePaths = "productos")
    Optional<CarritoEntity> findById(Long id);
}
