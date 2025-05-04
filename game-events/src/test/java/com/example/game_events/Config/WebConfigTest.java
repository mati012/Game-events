package com.example.game_events.Config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.example.game_events.config.WebConfig;

public class WebConfigTest {

    @Test
    public void testAddResourceHandlers() {
        // Arrange
        WebConfig webConfig = new WebConfig();
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);
        when(registry.addResourceHandler(anyString())).thenReturn(registration);
        
        // Act
        webConfig.addResourceHandlers(registry);
        
        // Assert
        verify(registry).addResourceHandler("/css/**");
        verify(registry).addResourceHandler("/js/**");
        verify(registry).addResourceHandler("/images/**");
    }
}