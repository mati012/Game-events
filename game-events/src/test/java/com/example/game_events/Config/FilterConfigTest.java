package com.example.game_events.Config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.example.game_events.config.FilterConfig;
import com.example.game_events.config.WebSecurityFilter;

public class FilterConfigTest {

    @Test
    public void testWebSecurityFilterRegistration() {
        // Arrange
        FilterConfig filterConfig = new FilterConfig();
        
        // Act
        FilterRegistrationBean<WebSecurityFilter> registrationBean = filterConfig.webSecurityFilter();
        
        // Assert
        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getFilter());
        assertTrue(registrationBean.getFilter() instanceof WebSecurityFilter);
        assertTrue(registrationBean.getUrlPatterns().contains("/*"));
        assertEquals(Integer.MIN_VALUE, registrationBean.getOrder());
        assertEquals("webSecurityFilter", registrationBean.getFilterName());
    }
}