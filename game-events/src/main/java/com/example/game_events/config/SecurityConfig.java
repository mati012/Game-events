package com.example.game_events.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.example.game_events.Service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                        "script-src 'self' 'unsafe-eval'; " +  
                        "style-src 'self' https://fonts.googleapis.com; " + 
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
                .addHeaderWriter((request, response) -> {
                    if (response.containsHeader("Set-Cookie")) {
                        String header = response.getHeader("Set-Cookie");
                        if (header.contains("JSESSIONID")) {
                            String newHeader = header;
                            if (!newHeader.contains("SameSite")) {
                                newHeader += "; SameSite=Strict";
                            }
                            response.setHeader("Set-Cookie", newHeader);
                        }
                    }
                })
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
    public jakarta.servlet.http.Cookie customCookieConfig() {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("JSESSIONID", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }
}