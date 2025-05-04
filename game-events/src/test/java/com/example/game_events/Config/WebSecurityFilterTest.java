package com.example.game_events.Config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

import com.example.game_events.config.WebSecurityFilter;

public class WebSecurityFilterTest {

    @Test
    public void testDoFilter() throws IOException, ServletException {
        // Arrange
        WebSecurityFilter filter = new WebSecurityFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        // Act
        filter.doFilter(request, response, chain);
        
        // Assert
        verify(response).setHeader("Content-Security-Policy", WebSecurityFilter.CSP_POLICY);
        verify(response).setHeader("X-Content-Type-Options", "nosniff");
        verify(response).setHeader("X-Frame-Options", "SAMEORIGIN");
        verify(response).setHeader("X-XSS-Protection", "1; mode=block");
        verify(response).setHeader("Referrer-Policy", "same-origin");
        verify(response).setHeader("Set-Cookie", "Path=/; HttpOnly; Secure; SameSite=Strict");
        
        verify(chain).doFilter(request, response);
    }
}