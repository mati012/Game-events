package com.example.game_events.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.game_events.Service.CustomUserDetailsService;
import com.example.game_events.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Configuración para endpoints de API con JWT
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")  // Esta configuración solo aplica a las rutas que comienzan con /api/
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    // Configuración para la aplicación web normal
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/home", "/css/**", "/js/**", "/webfonts/**", "/images/**",
                                    "/static/**", "/register", "/login", "/error").permitAll()
                    .requestMatchers("/events/search", "/events/*/details", "/events/**").authenticated()
                    .requestMatchers("/h2-console/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .defaultSuccessUrl("/events", true)
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .logoutSuccessUrl("/home")
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .headers(headers -> 
                headers
                    .contentSecurityPolicy(csp -> 
                        csp.policyDirectives(
                            "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +  // Permitir JavaScript en línea
                            "style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; "  + // Added 'unsafe-inline' for quick fix
                            "font-src 'self' https://fonts.gstatic.com; " +
                            "img-src 'self' data:; " +
                            "connect-src 'self'; " +
                            "frame-src 'self'; " +
                            "base-uri 'self'; " +
                            "form-action 'self'; " +
                            "frame-ancestors 'self'; " +
                            "object-src 'none'; " +
                            "upgrade-insecure-requests"
                        )
                    )
                    .referrerPolicy(referrer -> 
                        referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                    )
                    .frameOptions(frame -> frame.sameOrigin())
                    .xssProtection(xss -> xss.disable())  
            )
            .csrf(csrf -> 
                csrf.ignoringRequestMatchers("/h2-console/**") 
            )
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(true);
                config.addAllowedOriginPattern("http://localhost:[*]"); 
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");
                return config;
            }))
            .sessionManagement(session -> 
                session
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .maximumSessions(1)
                    .expiredUrl("/login?expired")
            );
        
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public HandlerInterceptor nonceInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String nonce = UUID.randomUUID().toString();
                request.setAttribute("nonce", nonce);
                return true;
            }
        };
    }
    
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(nonceInterceptor());
            }
        };
    }
}