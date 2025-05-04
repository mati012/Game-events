package com.example.game_events.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;

@SpringBootTest
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private Model model;

    @InjectMocks
    private EventController eventController;

    private Event event1, event2;
    private List<Event> eventList;

    @BeforeEach
    public void setup() {
        // Configurar datos de prueba
        event1 = new Event();
        event1.setId(1L);
        event1.setName("Test Event 1");
        event1.setDescription("Description 1");
        event1.setGameType("FPS");
        event1.setLocation("Location 1");

        event2 = new Event();
        event2.setId(2L);
        event2.setName("Test Event 2");
        event2.setDescription("Description 2");
        event2.setGameType("RPG");
        event2.setLocation("Location 2");

        eventList = Arrays.asList(event1, event2);

        when(model.addAttribute(anyString(), any())).thenReturn(model);
    }

    @Test
    public void testListEvents() {
        // Arrange
        when(eventService.getAllEvents()).thenReturn(eventList);

        // Act
        String viewName = eventController.listEvents(model);

        // Assert
        assertEquals("events/list", viewName);
        verify(eventService).getAllEvents();
        verify(model).addAttribute(eq("events"), eq(eventList));
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }

    @Test
    public void testSearchEvents() {
        // Arrange
        when(eventService.searchEvents("test", "FPS", "Location 1")).thenReturn(Arrays.asList(event1));

        // Act
        String viewName = eventController.searchEvents("test", "FPS", "Location 1", model);

        // Assert
        assertEquals("events/search", viewName);
        verify(eventService).searchEvents("test", "FPS", "Location 1");
        verify(model).addAttribute(eq("events"), eq(Arrays.asList(event1)));
        verify(model).addAttribute("keyword", "test");
        verify(model).addAttribute("gameType", "FPS");
        verify(model).addAttribute("location", "Location 1");
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }

    @Test
    public void testEventDetails() {
        // Arrange
        when(eventService.getEventById(1L)).thenReturn(Optional.of(event1));

        // Act
        String viewName = eventController.eventDetails(1L, model);

        // Assert
        assertEquals("events/details", viewName);
        verify(eventService).getEventById(1L);
        verify(model).addAttribute(eq("event"), eq(event1));
        verify(model).addAttribute(eq("nonce"), any(String.class));
    }

    @Test
    public void testEventDetailsNotFound() {
        // Arrange
        when(eventService.getEventById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            eventController.eventDetails(99L, model);
        });

        assertEquals("Event not found", exception.getMessage());

        verify(eventService).getEventById(99L);
        verify(model, never()).addAttribute(eq("event"), any(Event.class));
    }
}