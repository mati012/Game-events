package com.example.game_events.Config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.game_events.config.ErrorPageFilter;

public class ErrorPageFilterTest {

    private ErrorPageFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    public void setup() {
        filter = new ErrorPageFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    public void testDoFilterWithHttpResponse() throws IOException, ServletException {
        // Act
        filter.doFilter(request, response, chain);

        // Assert
        // Verificar que se llamó a chain.doFilter con la respuesta envuelta
        verify(chain).doFilter(eq(request), any(ErrorPageFilter.CSPResponseWrapper.class));
        
        // Verificar que se aplicaron las cabeceras de seguridad
        verify(response).setHeader(eq("Content-Security-Policy"), anyString());
        verify(response).setHeader(eq("X-Content-Type-Options"), eq("nosniff"));
        verify(response).setHeader(eq("X-Frame-Options"), eq("SAMEORIGIN"));
        verify(response).setHeader(eq("Referrer-Policy"), eq("same-origin"));
    }

    @Test
    public void testDoFilterWithNonHttpResponse() throws IOException, ServletException {
        // Arrange
        ServletResponse nonHttpResponse = mock(ServletResponse.class);

        // Act
        filter.doFilter(request, nonHttpResponse, chain);

        // Assert
        verify(chain).doFilter(request, nonHttpResponse);
    }

    @Test
    public void testCSPResponseWrapperSetStatus() {
        // Arrange
        ErrorPageFilter.CSPResponseWrapper wrapper = new ErrorPageFilter.CSPResponseWrapper(response);

        // Act
        wrapper.setStatus(404); // Error status

        // Assert
        verify(response).setStatus(404);
        // Para un status code de error, debería aplicar las cabeceras
        verify(response).setHeader(eq("Content-Security-Policy"), anyString());
        verify(response).setHeader(eq("X-Content-Type-Options"), eq("nosniff"));
        verify(response).setHeader(eq("X-Frame-Options"), eq("SAMEORIGIN"));
        verify(response).setHeader(eq("Referrer-Policy"), eq("same-origin"));
    }

    @Test
    public void testCSPResponseWrapperSetStatusSuccess() {
        // Arrange
        ErrorPageFilter.CSPResponseWrapper wrapper = new ErrorPageFilter.CSPResponseWrapper(response);

        // Act
        wrapper.setStatus(200); // Success status

        // Assert
        verify(response).setStatus(200);
        // Para un status code exitoso, no debería aplicar las cabeceras
        verify(response, never()).setHeader(eq("Content-Security-Policy"), anyString());
    }

    @Test
    public void testCSPResponseWrapperSendError() throws IOException {
        // Arrange
        ErrorPageFilter.CSPResponseWrapper wrapper = new ErrorPageFilter.CSPResponseWrapper(response);

        // Act
        wrapper.sendError(500);

        // Assert
        verify(response).sendError(500);
        verify(response).setHeader(eq("Content-Security-Policy"), anyString());
    }

    @Test
    public void testCSPResponseWrapperSendErrorWithMessage() throws IOException {
        // Arrange
        ErrorPageFilter.CSPResponseWrapper wrapper = new ErrorPageFilter.CSPResponseWrapper(response);

        // Act
        wrapper.sendError(400, "Bad Request");

        // Assert
        verify(response).sendError(400, "Bad Request");
        verify(response).setHeader(eq("Content-Security-Policy"), anyString());
    }

    @Test
    public void testAddSecurityHeadersWithCookie() {
        // Arrange
        when(response.containsHeader("Set-Cookie")).thenReturn(true);
        when(response.getHeader("Set-Cookie")).thenReturn("sessionId=123; Path=/");

        // Act - Usar método privado a través de doFilter
        try {
            filter.doFilter(request, response, chain);
        } catch (Exception e) {
            fail("No debería lanzar excepción");
        }

        // Assert
        verify(response).setHeader(eq("Set-Cookie"), eq("sessionId=123; Path=/; SameSite=Strict"));
    }

    @Test
    public void testAddSecurityHeadersWithCookieAlreadyHasSameSite() {
        // Arrange
        when(response.containsHeader("Set-Cookie")).thenReturn(true);
        when(response.getHeader("Set-Cookie")).thenReturn("sessionId=123; Path=/; SameSite=Lax");

        // Act - Usar método privado a través de doFilter
        try {
            filter.doFilter(request, response, chain);
        } catch (Exception e) {
            fail("No debería lanzar excepción");
        }

        // Assert
        verify(response, never()).setHeader(eq("Set-Cookie"), anyString());
    }
}