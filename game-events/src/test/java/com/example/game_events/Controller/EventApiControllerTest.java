package com.example.game_events.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;

@SpringBootTest
public class EventApiControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventApiController eventApiController;

    private Event event1, event2;
    private List<Event> eventList;

    @BeforeEach
    public void setup() {
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
    }

    @Test
    public void testGetAllEvents() {
        when(eventService.getAllEvents()).thenReturn(eventList);

        ResponseEntity<List<Event>> response = eventApiController.getAllEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Test Event 1", response.getBody().get(0).getName());
        assertEquals("Test Event 2", response.getBody().get(1).getName());

        verify(eventService).getAllEvents();
    }

    @Test
    public void testSearchEvents() {
        when(eventService.searchEvents("test", "FPS", "Location 1")).thenReturn(Arrays.asList(event1));

        ResponseEntity<List<Event>> response = eventApiController.searchEvents("test", "FPS", "Location 1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Event 1", response.getBody().get(0).getName());

        verify(eventService).searchEvents("test", "FPS", "Location 1");
    }

    @Test
    public void testGetEventById() {
        when(eventService.getEventById(1L)).thenReturn(Optional.of(event1));

        ResponseEntity<Event> response = eventApiController.getEventById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Event 1", response.getBody().getName());

        verify(eventService).getEventById(1L);
    }

    @Test
    public void testGetEventByIdNotFound() {
        when(eventService.getEventById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            eventApiController.getEventById(99L);
        });

        assertEquals("Event not found", exception.getMessage());

        verify(eventService).getEventById(99L);
    }
}