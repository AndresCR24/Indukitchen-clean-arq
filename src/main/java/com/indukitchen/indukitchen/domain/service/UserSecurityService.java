package com.indukitchen.indukitchen.domain.service;

import com.indukitchen.indukitchen.persistence.crud.UserCrudEntity;
import com.indukitchen.indukitchen.persistence.entity.ClienteEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService implements UserDetailsService {

    private final UserCrudEntity userCrudEntity;

    public UserSecurityService(UserCrudEntity userCrudEntity) {
        this.userCrudEntity = userCrudEntity;
    }

    @Override
    public UserDetails loadUserByUsername(String cedula) throws UsernameNotFoundException {
        ClienteEntity clienteEntity = this.userCrudEntity.findById(cedula)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + cedula));
        return User.builder()
                .username(clienteEntity.getCedula())
                .password(clienteEntity.getPassword())
                .roles("ADMIN")
                .accountLocked(clienteEntity.getLocked())
                .disabled(clienteEntity.getDisabled())
                .build();
    }
}
