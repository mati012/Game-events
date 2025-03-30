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
            headers.addHeaderWriter((request, response) -> {
                String nonce = java.util.UUID.randomUUID().toString().replace("-", "");
                
                response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'nonce-" + nonce + "'; " +
                    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                    "font-src 'self' https://fonts.gstatic.com; " +
                    "img-src 'self' data:; " +
                    "connect-src 'self'; " +
                    "frame-src 'self';"
                );

                request.setAttribute("cspNonce", nonce);
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
        }));

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
}