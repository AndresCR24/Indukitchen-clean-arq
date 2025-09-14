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

//        if (this.crudProductoEntity.findFirstByTitulo(productoDto.nombre()) != null) {
//            throw new MovieAlredyExistException(movieDto.title());
//        }
        FacturaEntity facturaEntity = this.facturaMapper.toEntity(facturaDto);
        //productoEntity.setDescripcion("Disponible");
        return this.facturaMapper.toDto(this.crudFacturaEntity.save(facturaEntity));
    }

    //Pensar si en la logica del negocio se podria actualizar una factura
    //O si se elimina la factura y simplemente se crea otra
//    @Override
//    public FacturaDto update(long id, UpdateFacturaDto updateFacturaDto) {
//        FacturaEntity facturaEntity = this.crudFacturaEntity.findById(id).orElse(null);
//
//        if (facturaEntity == null) return null;
//
//        //this.movieMapper.updateEntityFromDto(updateMovieDto, movieEntity);
//
//        //
//        clienteEntity.setCedula(updateClienteDto.cedula());
//        clienteEntity.setCedula(updateClienteDto.cedula());
//        clienteEntity.setNombre(updateClienteDto.nombre());
//        clienteEntity.setDireccion(updateClienteDto.direccion());
//        clienteEntity.setCorreo(updateClienteDto.correo());
//        clienteEntity.setTelefono(updateClienteDto.telefono());
//
//        return this.clienteMapper.toDto(this.crudClienteEntity.save(clienteEntity));
//    }

    @Override
    public void delete(long id) {
        this.crudFacturaEntity.deleteById(id);
    }
}
