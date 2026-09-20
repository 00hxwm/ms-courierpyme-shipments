package com.courierpyme.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public/status")
    public String publicStatus() {
        return "API CourierPyme operativa (público)";
    }

    @GetMapping("/protected/ping")
    public String pingProtected() {
        return "Acceso autenticado con éxito";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Bienvenido administrador de CourierPyme";
    }
}