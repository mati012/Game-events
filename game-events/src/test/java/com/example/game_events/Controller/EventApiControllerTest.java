package com.example.game_events.Controller;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EventApiControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventApiController eventApiController;

    private Event testEvent1;
    private Event testEvent2;
    private List<Event> eventList;

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
    }

    @Test
    public void testGetAllEvents() {
        // Configurar mock
        when(eventService.getAllEvents()).thenReturn(eventList);

        // Ejecutar método bajo prueba
        ResponseEntity<List<Event>> response = eventApiController.getAllEvents();

        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Test Event 1", response.getBody().get(0).getName());
        assertEquals("Test Event 2", response.getBody().get(1).getName());
    }

    @Test
    public void testSearchEvents() {
        // Configurar mock
        when(eventService.searchEvents("test", "Test Game Type 1", "Test Location 1"))
                .thenReturn(Arrays.asList(testEvent1));

        // Ejecutar método bajo prueba
        ResponseEntity<List<Event>> response = eventApiController.searchEvents(
                "test", "Test Game Type 1", "Test Location 1");

        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Event 1", response.getBody().get(0).getName());
    }

    @Test
    public void testGetEventById() {
        // Configurar mock
        when(eventService.getEventById(1L)).thenReturn(Optional.of(testEvent1));

        // Ejecutar método bajo prueba
        ResponseEntity<Event> response = eventApiController.getEventById(1L);

        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Event 1", response.getBody().getName());
    }
}