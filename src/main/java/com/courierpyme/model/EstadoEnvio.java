package com.courierpyme.model;

public enum EstadoEnvio {
    CREADO, ACEPTADO, EN_BODEGA, EN_RUTA, ENTREGADO, CANCELADO;

    public boolean puedePasarA(EstadoEnvio destino) {
        return switch (this) {
            case CREADO    -> destino == ACEPTADO || destino == CANCELADO;
            case ACEPTADO  -> destino == EN_BODEGA || destino == CANCELADO;
            case EN_BODEGA -> destino == EN_RUTA || destino == CANCELADO;
            case EN_RUTA   -> destino == ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }
}