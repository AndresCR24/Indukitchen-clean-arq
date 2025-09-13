package com.indukitchen.indukitchen.persistence.repository;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.repository.ClienteRepository;
import com.indukitchen.indukitchen.persistence.crud.CrudClienteEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import com.indukitchen.indukitchen.persistence.mapper.ClienteMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ClienteEntityRepository implements ClienteRepository {

    private final CrudClienteEntity crudClienteEntity;
    private final ClienteMapper clienteMapper;

    public ClienteEntityRepository(CrudClienteEntity crudClienteEntity, ClienteMapper clienteMapper) {
        this.crudClienteEntity = crudClienteEntity;
        this.clienteMapper = clienteMapper;
    }


    @Override
    public List<ClienteDto> getAll() {
        return this.clienteMapper.toDto(this.crudClienteEntity.findAll());
    }

    @Override
    public ClienteDto getById(String cedula) {
        ClienteEntity clienteEntity = this.crudClienteEntity.findById(cedula).orElse(null);
        return this.clienteMapper.toDto(clienteEntity);
    }

    @Override
    public ClienteDto save(ClienteDto clienteDto) {

//        if (this.crudProductoEntity.findFirstByTitulo(productoDto.nombre()) != null) {
//            throw new MovieAlredyExistException(movieDto.title());
//        }

        ClienteEntity clienteEntity = this.clienteMapper.toEntity(clienteDto);
        //productoEntity.setDescripcion("Disponible");
        return this.clienteMapper.toDto(this.crudClienteEntity.save(clienteEntity));
    }

    @Override
    public ClienteDto update(String cedula, UpdateClienteDto updateClienteDto) {
        ClienteEntity clienteEntity = this.crudClienteEntity.findById(cedula).orElse(null);

        if (clienteEntity == null) return null;

        //this.movieMapper.updateEntityFromDto(updateMovieDto, movieEntity);

        //
        clienteEntity.setCedula(updateClienteDto.cedula());
        clienteEntity.setCedula(updateClienteDto.cedula());
        clienteEntity.setNombre(updateClienteDto.nombre());
        clienteEntity.setDireccion(updateClienteDto.direccion());
        clienteEntity.setCorreo(updateClienteDto.correo());
        clienteEntity.setTelefono(updateClienteDto.telefono());

        return this.clienteMapper.toDto(this.crudClienteEntity.save(clienteEntity));
    }

    @Override
    public void delete(String cedula) {
        this.crudClienteEntity.deleteById(cedula);
    }
}
