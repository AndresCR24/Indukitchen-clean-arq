package com.indukitchen.indukitchen.persistence.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "detalles")
public class DetalleEntity {

    @EmbeddedId
    private DetalleId id;

    @MapsId("idCarrito") // mapea al campo idCarrito dentro de DetalleId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito", nullable = false)
    private CarritoEntity carrito;

    @MapsId("idProducto") // mapea al campo idProducto dentro de DetalleId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private ProductoEntity producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public DetalleId getId() {
        return id;
    }

    public void setId(DetalleId id) {
        this.id = id;
    }

    public CarritoEntity getCarrito() {
        return carrito;
    }

    public void setCarrito(CarritoEntity carrito) {
        this.carrito = carrito;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "DetalleEntity{" +
                "id=" + id +
                ", carrito=" + carrito +
                ", producto=" + producto +
                ", cantidad=" + cantidad +
                '}';
    }
}
