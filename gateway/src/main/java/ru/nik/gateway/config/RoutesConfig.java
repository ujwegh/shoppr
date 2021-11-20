package ru.nik.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth", r -> r.path("/hello")
                        .uri("lb://AUTH"))
                .route("products", r -> r.path("/v1/products/**")
                        .uri("lb://PRODUCTS"))
                .route("notifications", r -> r.path("/v1/notifications/**")
                        .uri("lb://NOTIFICATIONS"))
                .build();
    }
}
