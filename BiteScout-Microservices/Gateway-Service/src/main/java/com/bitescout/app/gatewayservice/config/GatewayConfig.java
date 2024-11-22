package com.bitescout.app.gatewayservice.config;

import com.bitescout.app.gatewayservice.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter filter;

    public GatewayConfig(JwtAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("authentication-service", r -> r.path("/v1/auth/**")
                        .uri("lb://AUTHENTICATION-SERVICE"))

                .route("notification-service", r -> r.path("/v1/notifications/**")
                        .filters(f -> f.filter(filter)) // Apply the JWT filter here
                        .uri("lb://NOTIFICATION-SERVICE"))

                .route("ranking-service", r -> r.path("/v1/ranking/**")
                        .uri("lb://RANKING-SERVICE"))

                .route("reservation-service", r -> r.path("/v1/reservations/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://RESERVATION-SERVICE"))

                .route("restaurant-service", r -> r.path("/v1/restaurants/**")
                        .uri("lb://RESTAURANT-SERVICE"))

                .route("review-service", r -> r.path("/v1/reviews/**")
                        .uri("lb://REVIEW-SERVICE"))

                .route("user-service", r -> r.path("/v1/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://USER-SERVICE"))

                .build();
    }
}