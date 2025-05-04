package com.example.game_events.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

@SpringBootTest
public class CustomErrorControllerTest {

    @InjectMocks
    private CustomErrorController customErrorController;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Model model;

    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        model = mock(Model.class);
    }

    @Test
    public void testHandleErrorNotFound() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.NOT_FOUND.value());

        // Act
        String viewName = customErrorController.handleError(request, response, model);

        // Assert
        assertEquals("error/404", viewName);
        verify(model).addAttribute("error", "Página no encontrada");
        verify(model).addAttribute("message", "Lo sentimos, la página que estás buscando no existe.");
        verify(response).setHeader(eq("Content-Security-Policy"), anyString());
        verify(response).setHeader("X-Content-Type-Options", "nosniff");
        verify(response).setHeader("X-Frame-Options", "SAMEORIGIN");
        verify(response).setHeader("Referrer-Policy", "same-origin");
    }

    @Test
    public void testHandleErrorForbidden() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.FORBIDDEN.value());

        // Act
        String viewName = customErrorController.handleError(request, response, model);

        // Assert
        assertEquals("error/403", viewName);
        verify(model).addAttribute("error", "Acceso denegado");
        verify(model).addAttribute("message", "No tienes permiso para acceder a este recurso.");
    }

    @Test
    public void testHandleErrorInternalServerError() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Act
        String viewName = customErrorController.handleError(request, response, model);

        // Assert
        assertEquals("error/500", viewName);
        verify(model).addAttribute("error", "Error interno del servidor");
        verify(model).addAttribute("message", "Ha ocurrido un error interno. Por favor, inténtalo más tarde.");
    }

    @Test
    public void testHandleErrorUnknownStatus() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.BAD_GATEWAY.value());

        // Act
        String viewName = customErrorController.handleError(request, response, model);

        // Assert
        assertEquals("error/general", viewName);
        verify(model).addAttribute("error", "Error");
        verify(model).addAttribute("message", "Ha ocurrido un error inesperado.");
    }

    @Test
    public void testHandleErrorNoStatus() {
        // Arrange
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        // Act
        String viewName = customErrorController.handleError(request, response, model);

        // Assert
        assertEquals("error/general", viewName);
        verify(model).addAttribute("error", "Error");
        verify(model).addAttribute("message", "Ha ocurrido un error inesperado.");
    }
}