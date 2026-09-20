package com.courierpyme.controller;

import com.courierpyme.model.Envio;
import com.courierpyme.model.EstadoEnvio;
import com.courierpyme.repository.EnvioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipments")
public class EnvioController {

    private static final Logger log = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioRepository envioRepository;

    public EnvioController(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public ResponseEntity<List<Envio>> listar(@RequestParam(required = false) String status) {
        List<Envio> resultado = (status == null || status.isBlank())
                ? envioRepository.findAll()
                : envioRepository.findByEstado(status.trim().toUpperCase());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return envioRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Envío no encontrado")));
    }

    @GetMapping("/tracking/{codigo}")
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public ResponseEntity<?> rastrear(@PathVariable String codigo) {
        log.info("Consulta de tracking");
        return envioRepository.findByCodigoSeguimiento(codigo)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Código de seguimiento no encontrado")));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Operador','Cliente')")
    public ResponseEntity<Envio> crear(@RequestBody Envio nuevo) {
        nuevo.setId(null); // evita sobrescribir un envío existente
        nuevo.setEstado(EstadoEnvio.CREADO.name());
        if (nuevo.getCodigoSeguimiento() == null || nuevo.getCodigoSeguimiento().isBlank()) {
            nuevo.setCodigoSeguimiento("RX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(envioRepository.save(nuevo));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('Admin','Operador')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        String raw = body.get("status");
        if (raw == null || raw.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Falta el campo status"));
        }

        EstadoEnvio destino;
        try {
            destino = EstadoEnvio.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Estado inválido",
                    "permitidos", Arrays.toString(EstadoEnvio.values())));
        }

        var opt = envioRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Envío no encontrado"));
        }

        Envio envio = opt.get();
        EstadoEnvio actual;
        try {
            actual = EstadoEnvio.valueOf(envio.getEstado());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El envío tiene un estado antiguo: " + envio.getEstado()));
        }

        if (!actual.puedePasarA(destino)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Transición no permitida: " + actual + " -> " + destino));
        }

        envio.setEstado(destino.name());
        return ResponseEntity.ok(envioRepository.save(envio));
    }
}