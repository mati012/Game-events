package com.example.game_events.Config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.game_events.config.SecurityConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest
public class SecurityConfigTest {

    @Test
    public void testCreatePasswordEncoder() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig(null, null);
        
        // Act
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        
        // Assert
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        
        // Verificar que funciona correctamente
        String password = "testPassword";
        String encoded = encoder.encode(password);
        assertTrue(encoder.matches(password, encoded));
        assertFalse(encoder.matches("wrongPassword", encoded));
    }

    @Test
    public void testNonceInterceptor() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig(null, null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Object handler = new Object();
        
        // Act
        HandlerInterceptor interceptor = securityConfig.nonceInterceptor();
        boolean result = false;
        try {
            result = interceptor.preHandle(request, response, handler);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Assert
        assertTrue(result);
        verify(request).setAttribute(eq("nonce"), any(String.class));
    }
    
    @Test
    public void testWebMvcConfigurer() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig(null, null);
        
        // Act
        Object configurer = securityConfig.webMvcConfigurer();
        
        // Assert
        assertNotNull(configurer);
    }
}