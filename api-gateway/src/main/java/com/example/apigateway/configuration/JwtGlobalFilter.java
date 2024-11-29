package com.example.apigateway.configuration;

import com.example.apigateway.configuration.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtGlobalFilter implements GlobalFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public JwtGlobalFilter() {
        log.info("JwtGlobalFilter initialized");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Start processing filter for request: {}", exchange.getRequest().getPath());

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Authorization Header: {}", authorizationHeader);

        // Kiểm tra Header Authorization
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Trích xuất token
        String token = authorizationHeader.substring(7);
        log.info("Extracted token: {}", token);

        // Xác thực token
        if (!jwtTokenProvider.validateToken(token)) {
            log.error("Invalid token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Trích xuất thông tin từ token
        Claims claims = jwtTokenProvider.getClaims(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);

        log.info("Token valid. User ID: {}, Role: {}", userId, role);

        // Bổ sung Header để gửi đến các microservices
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.add("X-User-Id", userId);
                    headers.add("X-Role", role);
                    // Có thể thêm thông tin khác nếu cần
                    headers.add("Authorization", authorizationHeader); // Forward original Authorization header
                }))
                .build();

        return chain.filter(mutatedExchange);
    }


}