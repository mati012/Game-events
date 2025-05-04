package com.example.game_events.Security;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.game_events.Service.JwtService;
import com.example.game_events.security.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        
        // Limpiar el contexto de seguridad antes de cada prueba
        SecurityContextHolder.clearContext();
        
        // Crear userDetails para las pruebas
        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    public void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid_token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid(token, userDetails);
    }
    
    @Test
    public void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        // Arrange
        String token = "invalid_token";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid(token, userDetails);
    }
    
    @Test
    public void testDoFilterInternal_WithNoToken() throws ServletException, IOException {
        // Arrange - No token in the request
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(jwtService, never()).extractUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
    }
    
    @Test
    public void testDoFilterInternal_WithInvalidAuthHeader() throws ServletException, IOException {
        // Arrange - Invalid auth header format
        request.addHeader("Authorization", "Invalid token");
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(jwtService, never()).extractUsername(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
    }
    
    @Test
    public void testDoFilterInternal_WithNullUsername() throws ServletException, IOException {
        // Arrange
        String token = "token_with_null_username";
        request.addHeader("Authorization", "Bearer " + token);
        
        when(jwtService.extractUsername(token)).thenReturn(null);
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(jwtService).extractUsername(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
    }
    
    @Test
    public void testDoFilterInternal_WithExistingAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid_token";
        request.addHeader("Authorization", "Bearer " + token);
        
        // Establecer una autenticación existente
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "existingUser", null, null));
        
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("existingUser", SecurityContextHolder.getContext().getAuthentication().getName());
        
        // Verificar que no se intentó procesar el token porque ya había una autenticación
        verify(jwtService).extractUsername(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
    }
}