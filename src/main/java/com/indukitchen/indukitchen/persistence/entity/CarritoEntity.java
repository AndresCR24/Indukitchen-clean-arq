package com.indukitchen.indukitchen.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Representa la entidad del carrito de compras.
 */
@Entity
@Table(name = "carritos")
public class CarritoEntity {
    /**
     * Identificador único del carrito.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Identificador del cliente asociado con el carrito.
     * Se refiere a la cédula del cliente.
     */
    @Column(name = "cedula_cliente", nullable = false)
    private String idCliente;

    /**
     * Fecha y hora de creación del carrito.
     * Este campo se llena automáticamente al crear el carrito.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del carrito.
     * Este campo se actualiza automáticamente cada vez que se modifica el carrito.
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Lista de detalles asociados con el carrito.
     * Cada detalle representa un producto en el carrito.
     * Se maneja la relación bidireccional con la entidad DetalleEntity.
     */
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<DetalleEntity> detalles;

    /**
     * Cliente asociado con el carrito.
     * Se maneja la relación bidireccional con la entidad ClienteEntity.
     */
    @ManyToOne
    @JoinColumn(name = "cedula_cliente", referencedColumnName = "cedula", insertable = false, updatable = false)
    private ClienteEntity cliente;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DetalleEntity> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleEntity> detalles) {
        this.detalles = detalles;
    }

    public ClienteEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClienteEntity cliente) {
        this.cliente = cliente;
    }
}
