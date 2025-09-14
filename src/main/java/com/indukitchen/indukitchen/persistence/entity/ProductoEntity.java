package com.indukitchen.indukitchen.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String nombre;

    @Column(length = 150)
    private String descripcion;

    @Column
    private BigDecimal precio;

    @Column
    private Integer existencia;

    @Column
    private Double peso;

    @Column
    private String imagen;

    /** Carritos que contienen este producto (lado inverso). */
    @ManyToMany(mappedBy = "productos")
    private List<CarritoEntity> carritos = new ArrayList<>();

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getExistencia() { return existencia; }
    public void setExistencia(Integer existencia) { this.existencia = existencia; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public List<CarritoEntity> getCarritos() { return carritos; }
    public void setCarritos(List<CarritoEntity> carritos) { this.carritos = carritos; }
}
