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

import com.example.game_events.Service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())  // Solo para desarrollo
        .authorizeHttpRequests(authorizeRequests ->
        authorizeRequests
            .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**",
                          "/static/**", "/register", "/login", "/error", "/h2-console/**").permitAll()
            .requestMatchers("/events/search", "/events/*/details", "/events/**").authenticated()
            .anyRequest().authenticated()
        )
        .formLogin(formLogin ->
            formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/events", true)  // Solo una URL de éxito
                .permitAll()
        )
        .logout(logout ->
            logout
                .logoutSuccessUrl("/home")
                .permitAll()
        )
        .headers(headers -> 
            headers.frameOptions(frameOptions -> frameOptions.sameOrigin())
        );
        
        // Configurar el proveedor de autenticación
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
    
    // Eliminar el método userDetailsService() que crea el usuario en memoria
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}