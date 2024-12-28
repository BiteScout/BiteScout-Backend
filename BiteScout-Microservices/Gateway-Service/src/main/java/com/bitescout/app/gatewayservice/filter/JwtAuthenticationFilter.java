package com.bitescout.app.gatewayservice.filter;

import com.bitescout.app.gatewayservice.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        final List<String> publicEndpoints = List.of("/auth", "/eureka", "/restaurants", "/reviews", "/users");
        Predicate<ServerHttpRequest> isApiSecured = r -> publicEndpoints.stream()
                .noneMatch(uri -> r.getURI().getPath().contains(uri));

        if (isApiSecured.test(request)) {
            if (authMissing(request)) return onError(exchange);

            String token = request.getHeaders().getOrEmpty("Authorization").get(0);

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            try {
                jwtUtil.validateToken(token);
            } catch (Exception e) {
                return onError(exchange);
            }

            // Add user-id to header for specific endpoints
            if (isSpecificEndpoint(request)) {
                String userId = jwtUtil.extractClaim(token, "User-id"); // Extract user-id from the token
                if (userId != null) {
                    System.out.println("User-id: " + userId);
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("User-id", userId)
                            .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                }
            }
        }

        return chain.filter(exchange);
    }

    private boolean isSpecificEndpoint(ServerHttpRequest request) {
        return request.getURI().getPath().contains("reservations") ||
                request.getURI().getPath().contains("notifications");
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
}