package com.example.game_events.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;


public class WebSecurityFilter implements Filter {

    public static final String CSP_POLICY = 
        "default-src 'self'; " +
        "script-src 'self' 'unsafe-eval'; " + // Si realmente necesitas eval(), mantenlo
        "style-src 'self' https://fonts.googleapis.com; " +
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Aplicar CSP a todas las respuestas
        httpResponse.setHeader("Content-Security-Policy", CSP_POLICY);
        
        // Aplicar otras cabeceras de seguridad importantes
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        httpResponse.setHeader("Referrer-Policy", "same-origin");
        
        // SameSite para cookies
        httpResponse.setHeader("Set-Cookie", "Path=/; HttpOnly; Secure; SameSite=Strict");
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, httpResponse);
    }


}