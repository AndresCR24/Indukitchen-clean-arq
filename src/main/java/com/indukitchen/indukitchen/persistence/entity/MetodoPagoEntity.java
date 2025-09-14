package com.indukitchen.indukitchen.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "metodos_pago")

public class MetodoPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Boolean efectivo;

    @Column
    private Boolean tarjeta;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(Boolean efectivo) {
        this.efectivo = efectivo;
    }

    public Boolean getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(Boolean tarjeta) {
        this.tarjeta = tarjeta;
    }

}

