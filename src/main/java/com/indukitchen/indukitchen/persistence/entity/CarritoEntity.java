package com.indukitchen.indukitchen.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa la entidad del carrito de compras.
 */
@Entity
@Table(name = "carritos")
public class CarritoEntity {

    /** Identificador único del carrito. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Identificador del cliente asociado con el carrito (cédula).
     * Se mantiene como columna simple y además relación @ManyToOne solo-lectura.
     */
    @Column(name = "cedula_cliente", nullable = false)
    private String idCliente;

    /** Fecha y hora de creación del carrito. */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** Fecha y hora de la última actualización del carrito. */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** Cliente asociado con el carrito. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cedula_cliente", referencedColumnName = "cedula",
            insertable = false, updatable = false)
    private ClienteEntity cliente;

    /** Productos en el carrito (relación Many-to-Many). */
    @ManyToMany
    @JoinTable(
            name = "carrito_productos",
            joinColumns = @JoinColumn(name = "carrito_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<ProductoEntity> productos = new ArrayList<>();

    // Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ProductoEntity> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoEntity> productos) {
        this.productos = productos;
    }

    public ClienteEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClienteEntity cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }
}
