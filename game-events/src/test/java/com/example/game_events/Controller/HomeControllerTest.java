package com.example.game_events.Controller;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HomeControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private HomeController homeController;

    private Event featuredEvent;
    private Event recentEvent;
    private List<Event> featuredEvents;
    private List<Event> recentEvents;
    private Model model;

    @BeforeEach
    public void setup() {
        // Configurar datos de prueba
        featuredEvent = new Event();
        featuredEvent.setId(1L);
        featuredEvent.setName("Featured Event");
        featuredEvent.setDescription("Featured Description");
        featuredEvent.setDateTime(LocalDateTime.now());
        featuredEvent.setLocation("Featured Location");
        featuredEvent.setGameType("Featured Game Type");
        featuredEvent.setFeatured(true);

        recentEvent = new Event();
        recentEvent.setId(2L);
        recentEvent.setName("Recent Event");
        recentEvent.setDescription("Recent Description");
        recentEvent.setDateTime(LocalDateTime.now().plusDays(1));
        recentEvent.setLocation("Recent Location");
        recentEvent.setGameType("Recent Game Type");
        recentEvent.setFeatured(false);

        featuredEvents = Arrays.asList(featuredEvent);
        recentEvents = Arrays.asList(recentEvent);

        model = mock(Model.class);
        when(model.addAttribute(any(String.class), any())).thenReturn(model);
    }

    @Test
    public void testHome() {
        // Configurar mocks
        when(eventService.getFeaturedEvents()).thenReturn(featuredEvents);
        when(eventService.getRecentEvents()).thenReturn(recentEvents);

        // Ejecutar método bajo prueba
        String viewName = homeController.home(model);

        // Verificar resultados
        assertEquals("home", viewName);
        verify(model).addAttribute(eq("featuredEvents"), eq(featuredEvents));
        verify(model).addAttribute(eq("recentEvents"), eq(recentEvents));
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }
}