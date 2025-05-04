package com.example.game_events.Model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AuthModelTests {

    @Test
    public void testAuthRequest() {
        // Constructor vacío
        AuthRequest authRequest1 = new AuthRequest();
        assertNull(authRequest1.getUsername());
        assertNull(authRequest1.getPassword());
        
        // Constructor con parámetros
        AuthRequest authRequest2 = new AuthRequest("testuser", "password");
        assertEquals("testuser", authRequest2.getUsername());
        assertEquals("password", authRequest2.getPassword());
        
        // Setters y getters
        authRequest1.setUsername("newuser");
        authRequest1.setPassword("newpassword");
        assertEquals("newuser", authRequest1.getUsername());
        assertEquals("newpassword", authRequest1.getPassword());
    }
    
    @Test
    public void testAuthResponse() {
        // Constructor vacío
        AuthResponse authResponse1 = new AuthResponse();
        assertNull(authResponse1.getToken());
        assertNull(authResponse1.getUsername());
        assertNull(authResponse1.getRole());
        
        // Constructor con parámetros
        AuthResponse authResponse2 = new AuthResponse("jwt_token", "testuser", "ROLE_USER");
        assertEquals("jwt_token", authResponse2.getToken());
        assertEquals("testuser", authResponse2.getUsername());
        assertEquals("ROLE_USER", authResponse2.getRole());
        
        // Setters y getters
        authResponse1.setToken("new_token");
        authResponse1.setUsername("newuser");
        authResponse1.setRole("ROLE_ADMIN");
        assertEquals("new_token", authResponse1.getToken());
        assertEquals("newuser", authResponse1.getUsername());
        assertEquals("ROLE_ADMIN", authResponse1.getRole());
    }
}