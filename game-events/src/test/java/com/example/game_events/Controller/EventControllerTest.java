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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private Event testEvent1;
    private Event testEvent2;
    private List<Event> eventList;
    private Model model;

    @BeforeEach
    public void setup() {
        // Configurar datos de prueba
        testEvent1 = new Event();
        testEvent1.setId(1L);
        testEvent1.setName("Test Event 1");
        testEvent1.setDescription("Test Description 1");
        testEvent1.setDateTime(LocalDateTime.now());
        testEvent1.setLocation("Test Location 1");
        testEvent1.setGameType("Test Game Type 1");
        testEvent1.setCurrentParticipants(5);
        testEvent1.setMaxParticipants(10);
        testEvent1.setFeatured(true);

        testEvent2 = new Event();
        testEvent2.setId(2L);
        testEvent2.setName("Test Event 2");
        testEvent2.setDescription("Test Description 2");
        testEvent2.setDateTime(LocalDateTime.now().plusDays(1));
        testEvent2.setLocation("Test Location 2");
        testEvent2.setGameType("Test Game Type 2");
        testEvent2.setCurrentParticipants(3);
        testEvent2.setMaxParticipants(8);
        testEvent2.setFeatured(false);

        eventList = Arrays.asList(testEvent1, testEvent2);
        model = mock(Model.class);
        when(model.addAttribute(any(String.class), any())).thenReturn(model);
    }

    @Test
    public void testListEvents() {
        // Configurar mock
        when(eventService.getAllEvents()).thenReturn(eventList);

        // Ejecutar método bajo prueba
        String viewName = eventController.listEvents(model);

        // Verificar resultados
        assertEquals("events/list", viewName);
        verify(model).addAttribute(eq("events"), eq(eventList));
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }

    @Test
    public void testSearchEvents() {
        // Configurar mock
        when(eventService.searchEvents("test", "Test Game Type 1", "Test Location 1"))
                .thenReturn(Arrays.asList(testEvent1));

        // Ejecutar método bajo prueba
        String viewName = eventController.searchEvents(
                "test", "Test Game Type 1", "Test Location 1", model);

        // Verificar resultados
        assertEquals("events/search", viewName);
        verify(model).addAttribute(eq("events"), eq(Arrays.asList(testEvent1)));
        verify(model).addAttribute(eq("keyword"), eq("test"));
        verify(model).addAttribute(eq("gameType"), eq("Test Game Type 1"));
        verify(model).addAttribute(eq("location"), eq("Test Location 1"));
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }

    @Test
    public void testEventDetails() {
        // Configurar mock
        when(eventService.getEventById(1L)).thenReturn(Optional.of(testEvent1));

        // Ejecutar método bajo prueba
        String viewName = eventController.eventDetails(1L, model);

        // Verificar resultados
        assertEquals("events/details", viewName);
        verify(model).addAttribute(eq("event"), eq(testEvent1));
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }
}