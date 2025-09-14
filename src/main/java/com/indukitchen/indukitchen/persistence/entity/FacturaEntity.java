package com.indukitchen.indukitchen.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "facturas")

public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "id_carrito", nullable = false)
    private long idCarrito;

    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @OneToOne
    @JoinColumn(name = "id_carrito", referencedColumnName = "id", insertable = false, updatable = false)
    private CarritoEntity carritoFactura;

    @OneToOne
    @JoinColumn(name = "id_metodo_pago", referencedColumnName = "id", insertable = false, updatable = false)
    private MetodoPagoEntity metodoPagoFactura;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(long idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Integer getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(Integer idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public CarritoEntity getCarritoFactura() {
        return carritoFactura;
    }

    public void setCarritoFactura(CarritoEntity carritoFactura) {
        this.carritoFactura = carritoFactura;
    }

    public MetodoPagoEntity getMetodoPagoFactura() {
        return metodoPagoFactura;
    }

    public void setMetodoPagoFactura(MetodoPagoEntity metodoPagoFactura) {
        this.metodoPagoFactura = metodoPagoFactura;
    }
}

