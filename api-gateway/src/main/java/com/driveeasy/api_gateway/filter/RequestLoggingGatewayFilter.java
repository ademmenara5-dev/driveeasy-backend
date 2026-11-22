package com.driveeasy.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingGatewayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        log.info("[API-GATEWAY] Requête entrante : {} {} depuis {}",
                request.getMethod(), request.getURI(), request.getRemoteAddress());

        // Traçage personnalisé : ajout d'un en-tête d'horodatage
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-DriveEasy-Gateway-Timestamp", String.valueOf(startTime))
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = mutatedExchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;
            log.info("[API-GATEWAY] Réponse sortante : {} {} | Statut: {} | Durée: {} ms",
                    request.getMethod(), request.getURI().getPath(), response.getStatusCode(), duration);
            response.getHeaders().add("X-Response-Time-Millis", String.valueOf(duration));
            response.getHeaders().add("X-Gateway-Processed-By", "DriveEasy-Spring-Cloud-Gateway");
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Haute priorité dans la chaîne de filtres
    }
}
