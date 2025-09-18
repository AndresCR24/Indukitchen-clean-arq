package com.indukitchen.indukitchen.persistence.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String direccion;
//    @JsonDeserialize(using = PointDeserializer.class)
//    @Column
//    private Point localizacion;

    @Column(name = "correo_electronico")
    private String correo;

    @Column(nullable = false, length = 17)
    private String telefono;

//    @Column()
//    private String password;
//

//    @Column()
//    private Boolean locked;
//
//    @Column()
//    private Boolean disabled;

    @OneToMany(mappedBy = "cliente")
    private List<CarritoEntity> carritos;

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<CarritoEntity> getCarritos() {
        return carritos;
    }

    public void setCarritos(List<CarritoEntity> carritos) {
        this.carritos = carritos;
    }

}
