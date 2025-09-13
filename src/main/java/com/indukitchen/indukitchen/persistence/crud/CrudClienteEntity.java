package com.indukitchen.indukitchen.persistence.crud;

import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.entity.ProductoEntity;
import org.springframework.data.repository.CrudRepository;

public interface CrudClienteEntity extends CrudRepository<ClienteEntity, String> {
    ClienteEntity findFirstByNombre(String nombre);
}
