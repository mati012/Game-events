package com.example.game_events.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    
    @Bean
    public FilterRegistrationBean<WebSecurityFilter> webSecurityFilter() {
        FilterRegistrationBean<WebSecurityFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new WebSecurityFilter());
        registrationBean.addUrlPatterns("/*"); // Aplicar a todas las URL
        registrationBean.setOrder(Integer.MIN_VALUE); // Máxima prioridad
        registrationBean.setName("webSecurityFilter");
        return registrationBean;
    }
}