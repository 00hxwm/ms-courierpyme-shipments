package com.courierpyme.controller;

import com.courierpyme.model.Envio;
import com.courierpyme.repository.EnvioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioRepository envioRepository;

    public EnvioController(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    // Endpoint de prueba rápida
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("PONG - ms-courierpyme-envios funcionando");
    }

    // Consulta pública por código de seguimiento
    @GetMapping("/tracking/{codigo}")
    public ResponseEntity<?> rastrearEnvio(@PathVariable("codigo") String codigo) {
        System.out.println("=================================================");
        System.out.println(">>> PETICION RECIBIDA PARA CODIGO: [" + codigo + "]");

        var resultado = envioRepository.findByCodigoSeguimiento(codigo);

        if (resultado.isPresent()) {
            System.out.println(">>> ENCONTRADO: " + resultado.get().getDestinatario());
            return ResponseEntity.ok(resultado.get());
        } else {
            System.out.println(">>> NO ENCONTRADO EN BASE DE DATOS");
            return ResponseEntity.status(404).body("{\"error\": \"No existe el codigo " + codigo + "\"}");
        }
    }

    // Listar todos los envíos
    @GetMapping
    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }

    // Crear un nuevo envío aplicando la regla de negocio
    @PostMapping
    public ResponseEntity<Envio> crearEnvio(@RequestBody Envio nuevoEnvio) {
        nuevoEnvio.setEstado("CREADO"); // Regla obligatoria del caso
        
        Envio envioGuardado = envioRepository.save(nuevoEnvio);
        return new ResponseEntity<>(envioGuardado, HttpStatus.CREATED);
    }
}