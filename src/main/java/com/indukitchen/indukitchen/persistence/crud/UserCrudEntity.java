package com.indukitchen.indukitchen.persistence.crud;

import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserCrudEntity extends CrudRepository<ClienteEntity, String> {
}
