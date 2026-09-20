package com.courierpyme.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_seguimiento", nullable = false, unique = true)
private String codigoSeguimiento;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String estado; // EJ: PENDIENTE, EN_TRANSITO, ENTREGADO

    private LocalDateTime fechaCreacion;

    public Envio() {
    }

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "CREADO";
        }
        if (this.codigoSeguimiento == null || this.codigoSeguimiento.isBlank()) {
            this.codigoSeguimiento = "RX-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}