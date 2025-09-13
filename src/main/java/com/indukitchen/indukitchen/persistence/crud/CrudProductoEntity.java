package com.indukitchen.indukitchen.persistence.crud;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import org.springframework.data.repository.CrudRepository;

public interface CrudProductoEntity extends CrudRepository<ProductoEntity, Long> {
    ProductoEntity findFirstByNombre(String nombre);
}
