package com.indukitchen.indukitchen.persistence.repository;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.repository.FacturaRepository;
import com.indukitchen.indukitchen.persistence.crud.CrudFacturaEntity;
import com.indukitchen.indukitchen.persistence.entity.FacturaEntity;
import com.indukitchen.indukitchen.persistence.mapper.FacturaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FacturaEntityRepository implements FacturaRepository {

    private final CrudFacturaEntity crudFacturaEntity;
    private final FacturaMapper facturaMapper;

    public FacturaEntityRepository(CrudFacturaEntity crudFacturaEntity, FacturaMapper facturaMapper) {
        this.crudFacturaEntity = crudFacturaEntity;
        this.facturaMapper = facturaMapper;
    }


    @Override
    public List<FacturaDto> getAll() {
        return this.facturaMapper.toDto(this.crudFacturaEntity.findAll());
    }

    @Override
    public FacturaDto getById(long id) {
        FacturaEntity facturaEntity = this.crudFacturaEntity.findById(id).orElse(null);
        return this.facturaMapper.toDto(facturaEntity);
    }

    @Override
    public FacturaDto save(FacturaDto facturaDto) {

        FacturaEntity facturaEntity = this.facturaMapper.toEntity(facturaDto);
        return this.facturaMapper.toDto(this.crudFacturaEntity.save(facturaEntity));
    }


    @Override
    public void delete(long id) {
        this.crudFacturaEntity.deleteById(id);
    }
}
