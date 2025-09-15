package com.indukitchen.indukitchen.persistence.crud;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrudProductoEntity extends JpaRepository<ProductoEntity, Long> {
    ProductoEntity findFirstByNombre(String nombre);
}
