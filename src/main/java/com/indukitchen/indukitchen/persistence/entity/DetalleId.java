package com.indukitchen.indukitchen.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DetalleId implements Serializable {

    private Long idProducto;
    private Long idCarrito;

    public DetalleId() {}

    public DetalleId(Long idProducto, Long idCarrito) {
        this.idProducto = idProducto;
        this.idCarrito = idCarrito;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Long idCarrito) {
        this.idCarrito = idCarrito;
    }

    @Override
    public String toString() {
        return "DetalleId{" +
                "idProducto=" + idProducto +
                ", idCarrito=" + idCarrito +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DetalleId detalleId = (DetalleId) o;
        return Objects.equals(idProducto, detalleId.idProducto) && Objects.equals(idCarrito, detalleId.idCarrito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto, idCarrito);
    }
}

