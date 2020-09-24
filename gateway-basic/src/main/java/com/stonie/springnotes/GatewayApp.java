package com.stonie.springnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApp {

    public static void main( String[] args ) {
        SpringApplication.run(GatewayApp.class, args);
    }

    /**
     * curl -H Content-Type:application/json -X POST --data '{"hello":"world"}' http://127.0.0.1:8080/post
     */
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/post")
                        .uri("http://httpbin.org:80"))
                .build();
    }
}
