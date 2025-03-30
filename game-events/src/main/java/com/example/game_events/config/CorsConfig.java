package com.example.game_events.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        config.setAllowedOrigins(List.of("http://localhost:8080", "http://example.com"));

        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Origin"
        ));

        config.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS"
        ));

        config.addExposedHeader("Authorization");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}