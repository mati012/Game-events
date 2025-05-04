package com.example.game_events.Config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.apache.catalina.Container;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import com.example.game_events.config.TomcatErrorPageCustomizer;

public class TomcatErrorPageCustomizerTest {

    private TomcatErrorPageCustomizer customizer;

    @BeforeEach
    public void setup() {
        customizer = new TomcatErrorPageCustomizer();
    }

    @Test
    public void testErrorValveCustomizer() {
        // Arrange
        WebServerFactoryCustomizer<TomcatServletWebServerFactory> factoryCustomizer = customizer.errorValveCustomizer();
        TomcatServletWebServerFactory factory = mock(TomcatServletWebServerFactory.class);

        // Act
        factoryCustomizer.customize(factory);

        // Assert
        verify(factory).addContextCustomizers(any());
    }

    @Test
    public void testCustomErrorReportValve() {
        // Arrange
        TomcatErrorPageCustomizer.CustomErrorReportValve valve = new TomcatErrorPageCustomizer.CustomErrorReportValve();
        Request request = mock(Request.class);
        Response response = mock(Response.class);
        Throwable throwable = new RuntimeException("Test exception");

        // Act
        valve.report(request, response, throwable);

        // Assert - Verificar que se aplicaron las cabeceras de seguridad
        verify(response).setHeader(eq("Content-Security-Policy"), anyString());
        verify(response).setHeader("X-Content-Type-Options", "nosniff");
        verify(response).setHeader("X-Frame-Options", "SAMEORIGIN");
        verify(response).setHeader("Referrer-Policy", "same-origin");
    }

   
}