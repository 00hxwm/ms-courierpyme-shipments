package com.courierpyme.repository;

import com.courierpyme.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);
    List<Envio> findByEstado(String estado);
}