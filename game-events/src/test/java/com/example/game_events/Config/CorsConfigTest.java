package com.example.game_events.Config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.game_events.config.CorsConfig;

@SpringBootTest
public class CorsConfigTest {

    @Autowired
    private CorsConfig corsConfig;

    @Test
    public void testCorsFilter() {
        // Act
        CorsFilter filter = corsConfig.corsFilter();
        UrlBasedCorsConfigurationSource source;
        try {
            java.lang.reflect.Field field = CorsFilter.class.getDeclaredField("configSource");
            field.setAccessible(true);
            source = (UrlBasedCorsConfigurationSource) field.get(filter);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access configSource field in CorsFilter", e);
        }
        CorsConfiguration config = source.getCorsConfigurations().get("/**");
        
        // Assert
        assertNotNull(filter);
        assertNotNull(config);
        assertTrue(config.getAllowCredentials());
        assertTrue(config.getAllowedOrigins().contains("http://localhost:8080"));
        assertTrue(config.getAllowedOrigins().contains("http://example.com"));
        assertTrue(config.getAllowedHeaders().contains("Authorization"));
        assertTrue(config.getAllowedHeaders().contains("Content-Type"));
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getExposedHeaders().contains("Authorization"));
    }
}