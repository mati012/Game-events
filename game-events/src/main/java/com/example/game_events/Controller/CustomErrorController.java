package com.example.game_events.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    // La política CSP para aplicar a páginas de error
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

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        
        response.setHeader("Content-Security-Policy", CSP_POLICY);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("Referrer-Policy", "same-origin");
        
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "Página no encontrada");
                model.addAttribute("message", "Lo sentimos, la página que estás buscando no existe.");
                return "error/404";
            }
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "Acceso denegado");
                model.addAttribute("message", "No tienes permiso para acceder a este recurso.");
                return "error/403";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("error", "Error interno del servidor");
                model.addAttribute("message", "Ha ocurrido un error interno. Por favor, inténtalo más tarde.");
                return "error/500";
            }
        }
        
        model.addAttribute("error", "Error");
        model.addAttribute("message", "Ha ocurrido un error inesperado.");
        return "error/general";
    }
}