package com.courierpyme.messaging;

import com.courierpyme.model.Envio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ShipmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ShipmentEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String emailRoutingKey;

    public ShipmentEventPublisher(RabbitTemplate rabbitTemplate,
                                  @Value("${app.rabbit.exchange}") String exchange,
                                  @Value("${app.rabbit.routing-key.email}") String emailRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.emailRoutingKey = emailRoutingKey;
    }

    public void publicarEnvioCreado(Envio envio) {
        try {
            String id = UUID.randomUUID().toString();
            String json = String.format(
                "{\"type\":\"SHIPMENT_CREATED\",\"eventId\":\"%s\",\"timestamp\":\"%s\","
              + "\"traceId\":\"%s\",\"correlationId\":\"%s\",\"shipmentId\":%d,"
              + "\"codigoSeguimiento\":\"%s\",\"destinatario\":\"%s\"}",
                id, Instant.now(), UUID.randomUUID(), id,
                envio.getId(), esc(envio.getCodigoSeguimiento()), esc(envio.getDestinatario()));

            rabbitTemplate.convertAndSend(exchange, emailRoutingKey, json);
            log.info("Evento publicado: exchange={} rk={} codigo={}",
                     exchange, emailRoutingKey, envio.getCodigoSeguimiento());
        } catch (Exception e) {
            // Si RabbitMQ falla, el envío igual queda creado
            log.error("No se pudo publicar el evento del envío {}: {}", envio.getId(), e.getMessage());
        }
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}