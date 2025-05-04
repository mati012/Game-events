package com.example.game_events.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    
    @BeforeEach
    public void setup() {
        jwtService = new JwtService();
        
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
    }
    
    @Test
    public void testGenerateToken() {
        // Act
        String token = jwtService.generateToken(userDetails);
        
        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 20);
        
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
        
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
    
    @Test
    public void testGenerateTokenWithExtraClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("userId", 123);
        
        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);
        
        // Assert
        assertNotNull(token);
        
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
        
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ADMIN", role);
        
        Integer userId = jwtService.extractClaim(token, claims -> claims.get("userId", Integer.class));
        assertEquals(123, userId);
        
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
    
    @Test
    public void testIsTokenValidWithDifferentUser() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        
        UserDetails differentUser = mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("anotheruser");
        
        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, differentUser));
    }
    
    @Test
    public void testExtractClaim() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        
        // Act
        String username = jwtService.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        
        // Assert
        assertEquals("testuser", username);
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // El token no ha expirado
    }
}