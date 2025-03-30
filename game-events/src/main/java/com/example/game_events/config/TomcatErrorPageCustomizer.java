package com.example.game_events.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.catalina.Container;
import org.apache.catalina.core.StandardHost;

@Configuration
public class TomcatErrorPageCustomizer {

    /**
     * Personaliza la fábrica de Tomcat para añadir un valve personalizado
     * que intercepta las páginas de error generadas por Tomcat.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> errorValveCustomizer() {
        return factory -> {
            factory.addContextCustomizers(context -> {
                Container parent = context.getParent();
                if (parent instanceof StandardHost) {
                    StandardHost host = (StandardHost) parent;
                    
                    // Reemplazar el valve de informes de error predeterminado
                    java.util.Arrays.stream(host.getPipeline().getValves())
                        .filter(valve -> valve instanceof ErrorReportValve)
                        .findFirst()
                        .ifPresent(host.getPipeline()::removeValve);
                    
                    // Añadir nuestro valve personalizado
                    CustomErrorReportValve valve = new CustomErrorReportValve();
                    host.getPipeline().addValve(valve);
                }
            });
        };
    }
    
    /**
     * Valve personalizado que añade cabeceras CSP a las páginas de error generadas por Tomcat.
     */
    public static class CustomErrorReportValve extends ErrorReportValve {
        
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
        protected void report(org.apache.catalina.connector.Request request, 
                              org.apache.catalina.connector.Response response, 
                              java.lang.Throwable throwable) {
            
            // Aplicar cabeceras de seguridad
            response.setHeader("Content-Security-Policy", CSP_POLICY);
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            response.setHeader("Referrer-Policy", "same-origin");
            
            // Continuar con el procesamiento normal
            super.report(request, response, throwable);
        }
    }
}