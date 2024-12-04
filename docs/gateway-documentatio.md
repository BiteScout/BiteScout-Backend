### Gateway Documentation

The gateway acts as an entry point for all client requests. It forwards these requests to the appropriate microservices based on the request path and method. This setup is typically achieved using Spring Cloud Gateway.

#### Key Components

1. **Gateway Configuration**: Defines the routes and filters for forwarding requests. The configuration specifies which paths should be routed to which microservices and applies any necessary filters to the requests.
2. **Controllers**: Each microservice has its own set of controllers to handle the requests forwarded by the gateway. These controllers process the requests and return the appropriate responses.
3. **Security**: The gateway can handle security concerns, such as authentication and authorization, before forwarding the requests to the microservices. This ensures that only authenticated and authorized requests are processed by the microservices.
### JWT Authentication Filter Documentation

The `JwtAuthenticationFilter` class is a custom filter that intercepts incoming requests to the gateway and validates the JWT tokens. This ensures that only authenticated requests are forwarded to the microservices.

#### Key Components

1. **JwtUtil**: A utility class used to validate JWT tokens.
2. **GatewayFilter**: An interface that the `JwtAuthenticationFilter` implements to create a custom filter for the gateway.
3. **ServerWebExchange**: Represents the web request and response.

#### How It Works

1. **Filter Method**: The `filter` method is the main entry point for the filter logic. It intercepts each request and checks if the request path is secured.
2. **Public Endpoints**: A list of endpoints that do not require authentication (e.g., `/auth`, `/eureka`, `/restaurants`, `/reviews`).
3. **isApiSecured Predicate**: A predicate that checks if the request path is not in the list of public endpoints.
4. **Token Extraction and Validation**:
    - If the request path is secured, the filter checks for the presence of the `Authorization` header.
    - If the header is missing, the request is rejected with a `401 Unauthorized` status.
    - If the header is present, the token is extracted and validated using the `JwtUtil` class.
    - If the token is invalid, the request is rejected with a `401 Unauthorized` status.
5. **Chain Filtering**: If the token is valid or the request path is public, the request is forwarded to the next filter in the chain.

#### Example Code

```java
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

        final List<String> publicEndpoints = List.of("/auth", "/eureka", "/restaurants", "/reviews");

        Predicate<ServerHttpRequest> isApiSecured = r -> publicEndpoints.stream()
                .noneMatch(uri -> r.getURI().getPath().contains(uri));

        if (isApiSecured.test(request)) {
            if (authMissing(request)) return onError(exchange);

            String token = request.getHeaders().getOrEmpty("Authorization").get(0);

            if (token != null && token.startsWith("Bearer ")) token = token.substring(7);

            try {
                jwtUtil.validateToken(token);

            } catch (Exception e) {
                return onError(exchange);
            }
        }
        return chain.filter(exchange);
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
```

This filter ensures that only authenticated requests with valid JWT tokens are allowed to access secured endpoints in your microservices architecture.
#### Summary

- **Gateway Configuration**: Routes and filters requests to the appropriate microservices.
- **Controllers**: Handle the forwarded requests in the respective microservices.
- **Security**: Ensures that only authenticated and authorized requests are processed.

This setup allows your gateway to efficiently route and secure requests in your microservices architecture.