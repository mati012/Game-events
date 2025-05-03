package com.example.game_events.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void testHandleError404() {
        // Configurar mocks
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.NOT_FOUND.value());

        // Ejecutar método bajo prueba
        String viewName = customErrorController.handleError(request, response, model);

        // Verificar resultados
        assertEquals("error/404", viewName);
        verify(model).addAttribute(eq("error"), eq("Página no encontrada"));
        verify(model).addAttribute(eq("message"), eq("Lo sentimos, la página que estás buscando no existe."));
        verify(response).setHeader(eq("Content-Security-Policy"), any(String.class));
    }

    @Test
    public void testHandleError403() {
        // Configurar mocks
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.FORBIDDEN.value());

        // Ejecutar método bajo prueba
        String viewName = customErrorController.handleError(request, response, model);

        // Verificar resultados
        assertEquals("error/403", viewName);
        verify(model).addAttribute(eq("error"), eq("Acceso denegado"));
        verify(model).addAttribute(eq("message"), eq("No tienes permiso para acceder a este recurso."));
    }

    @Test
    public void testHandleError500() {
        // Configurar mocks
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Ejecutar método bajo prueba
        String viewName = customErrorController.handleError(request, response, model);

        // Verificar resultados
        assertEquals("error/500", viewName);
        verify(model).addAttribute(eq("error"), eq("Error interno del servidor"));
        verify(model).addAttribute(eq("message"), eq("Ha ocurrido un error interno. Por favor, inténtalo más tarde."));
    }

    @Test
    public void testHandleGenericError() {
        // Configurar mocks
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);

        // Ejecutar método bajo prueba
        String viewName = customErrorController.handleError(request, response, model);

        // Verificar resultados
        assertEquals("error/general", viewName);
        verify(model).addAttribute(eq("error"), eq("Error"));
        verify(model).addAttribute(eq("message"), eq("Ha ocurrido un error inesperado."));
    }
}