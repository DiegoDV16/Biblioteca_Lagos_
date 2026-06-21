package com.bibliotecaLagos.gateway.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestController
public class GatewayController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/**")
    public Mono<ResponseEntity<String>> route(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getRawPath();
        String method = exchange.getRequest().getMethod().name();

        String targetUrl = resolveTargetUrl(path);
        if (targetUrl == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        return webClientBuilder.build()
                .method(HttpMethod.valueOf(method))
                .uri(targetUrl)
                .headers(headers -> {
                    exchange.getRequest().getHeaders().forEach((key, values) -> {
                        if (!"host".equalsIgnoreCase(key)) {
                            values.forEach(value -> headers.add(key, value));
                        }
                    });
                })
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.status(502)
                                .body("Error en gateway: " + e.getMessage())));
    }

    private String resolveTargetUrl(String path) {
        if (path.startsWith("/api/v1/categorias") || path.startsWith("/auth")) {
            return "http://localhost:8082" + path;
        }
        if (path.startsWith("/api/v1/libros")) {
            return "http://localhost:8081" + path;
        }
        if (path.startsWith("/api/v1/proveedores")) {
            return "http://localhost:8083" + path;
        }
        if (path.startsWith("/api/v1/tipos-socio")) {
            return "http://localhost:8086" + path;
        }
        if (path.startsWith("/api/v1/roles")) {
            return "http://localhost:8088" + path;
        }
        if (path.startsWith("/api/v1/socios")) {
            return "http://localhost:8085" + path;
        }

        return null;
    }
}
