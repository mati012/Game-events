package com.example.game_events.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * Filtro para aplicar cabeceras CSP a todas las respuestas,
 * incluyendo las páginas de error generadas por Tomcat.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorPageFilter implements Filter {

    // Definición de la política CSP
    private static final String CSP_POLICY = 
        "default-src 'self'; " +
        "script-src 'self' 'unsafe-eval'; " +
        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
        "font-src 'self' https://fonts.gstatic.com; " +
        "img-src 'self' data:; " +
        "connect-src 'self'; " +
        "frame-src 'self'; " +
        "base-uri 'self'; " +
        "form-action 'self'; " +
        "frame-ancestors 'self'; " +
        "object-src 'none'; " +
        "upgrade-insecure-requests";

 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // Envolvemos la respuesta para poder manipularla después
            CSPResponseWrapper wrappedResponse = new CSPResponseWrapper(httpResponse);
            
            try {
                // Continuamos con la cadena de filtros
                chain.doFilter(request, wrappedResponse);
            } finally {
                // Ya sea que haya ocurrido un error o no, aplicamos las cabeceras CSP
                addSecurityHeaders(wrappedResponse);
            }
        } else {
            // Si no es una respuesta HTTP, continuamos con la cadena de filtros
            chain.doFilter(request, response);
        }
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Aplicar cabecera CSP
        response.setHeader("Content-Security-Policy", CSP_POLICY);
        
        // Otras cabeceras de seguridad importantes
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("Referrer-Policy", "same-origin");
        
        // SameSite para cookies si no está ya configurado
        if (response.containsHeader("Set-Cookie")) {
            String cookie = response.getHeader("Set-Cookie");
            if (!cookie.contains("SameSite")) {
                response.setHeader("Set-Cookie", cookie + "; SameSite=Strict");
            }
        }
    }

    @Override
    public void destroy() {
        // No se requiere limpieza específica
    }
    
    /**
     * Wrapper para HttpServletResponse que permite aplicar cabeceras
     * después de que la respuesta se ha generado.
     */
    private static class CSPResponseWrapper extends HttpServletResponseWrapper {
        
        private boolean headersApplied = false;
        
        public CSPResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void setStatus(int sc) {
            super.setStatus(sc);
            
            // Si es una respuesta de error, aplicamos las cabeceras inmediatamente
            if (sc >= 400 && !headersApplied) {
                applyHeaders();
            }
        }
        
        @Override
        public void sendError(int sc) throws IOException {
            // Aplicar cabeceras antes de enviar el error
            if (!headersApplied) {
                applyHeaders();
            }
            super.sendError(sc);
        }
        
        @Override
        public void sendError(int sc, String msg) throws IOException {
            // Aplicar cabeceras antes de enviar el error con mensaje
            if (!headersApplied) {
                applyHeaders();
            }
            super.sendError(sc, msg);
        }
        
        private void applyHeaders() {
            this.setHeader("Content-Security-Policy", CSP_POLICY);
            this.setHeader("X-Content-Type-Options", "nosniff");
            this.setHeader("X-Frame-Options", "SAMEORIGIN");
            this.setHeader("Referrer-Policy", "same-origin");
            headersApplied = true;
        }
    }
}