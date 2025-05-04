package com.example.game_events.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;

@SpringBootTest
public class HomeControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    private Event featuredEvent, recentEvent;
    private List<Event> featuredEvents, recentEvents;

    @BeforeEach
    public void setup() {
        // Configurar datos de prueba
        featuredEvent = new Event();
        featuredEvent.setId(1L);
        featuredEvent.setName("Featured Event");
        featuredEvent.setFeatured(true);

        recentEvent = new Event();
        recentEvent.setId(2L);
        recentEvent.setName("Recent Event");

        featuredEvents = Arrays.asList(featuredEvent);
        recentEvents = Arrays.asList(recentEvent);

        when(model.addAttribute(anyString(), any())).thenReturn(model);
    }

    @Test
    public void testHome() {
        // Arrange
        when(eventService.getFeaturedEvents()).thenReturn(featuredEvents);
        when(eventService.getRecentEvents()).thenReturn(recentEvents);

        // Act
        String viewName = homeController.home(model);

        // Assert
        assertEquals("home", viewName);
        verify(eventService).getFeaturedEvents();
        verify(eventService).getRecentEvents();
        verify(model).addAttribute(eq("featuredEvents"), eq(featuredEvents));
        verify(model).addAttribute(eq("recentEvents"), eq(recentEvents));
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }

    @Test
    public void testHomeWithNoEvents() {
        // Arrange
        when(eventService.getFeaturedEvents()).thenReturn(Arrays.asList());
        when(eventService.getRecentEvents()).thenReturn(Arrays.asList());

        // Act
        String viewName = homeController.home(model);

        // Assert
        assertEquals("home", viewName);
        verify(eventService).getFeaturedEvents();
        verify(eventService).getRecentEvents();
        verify(model).addAttribute(eq("featuredEvents"), eq(Arrays.asList()));
        verify(model).addAttribute(eq("recentEvents"), eq(Arrays.asList()));
    }
}