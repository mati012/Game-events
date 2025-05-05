package com.example.game_events.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.game_events.Model.Event;
import com.example.game_events.Repository.EventRepository;

public class EventServiceImplTest {

    private EventServiceImpl eventService;
    private EventRepository eventRepository;
    private Event event1, event2, event3;
    
    @BeforeEach
    public void setup() {
        eventRepository = mock(EventRepository.class);
        eventService = new EventServiceImpl(eventRepository);
        
        event1 = new Event();
        event1.setId(1L);
        event1.setName("Event 1");
        event1.setDescription("Description 1");
        event1.setGameType("FPS");
        event1.setLocation("Location A");
        event1.setDateTime(LocalDateTime.now());
        event1.setFeatured(true);
        
        event2 = new Event();
        event2.setId(2L);
        event2.setName("Event 2");
        event2.setDescription("Description 2");
        event2.setGameType("RPG");
        event2.setLocation("Location B");
        event2.setDateTime(LocalDateTime.now().plusDays(1));
        event2.setFeatured(false);
        
        event3 = new Event();
        event3.setId(3L);
        event3.setName("FPS Tournament");
        event3.setDescription("Big tournament");
        event3.setGameType("FPS");
        event3.setLocation("Location A");
        event3.setDateTime(LocalDateTime.now().minusDays(1));
        event3.setFeatured(true);
    }
    
    @Test
    public void testGetAllEvents() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2, event3));
        
        // Act
        List<Event> result = eventService.getAllEvents();
        
        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event2));
        assertTrue(result.contains(event3));
        
        verify(eventRepository).findAll();
    }
    
    @Test
    public void testGetEventById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());
        
        Optional<Event> foundEvent = eventService.getEventById(1L);
        Optional<Event> notFoundEvent = eventService.getEventById(99L);
        
        assertTrue(foundEvent.isPresent());
        assertEquals("Event 1", foundEvent.get().getName());
        assertFalse(notFoundEvent.isPresent());
        
        verify(eventRepository).findById(1L);
        verify(eventRepository).findById(99L);
    }
    
    @Test
    public void testGetFeaturedEvents() {
        when(eventRepository.findByFeaturedTrue()).thenReturn(Arrays.asList(event1, event3));
        
        List<Event> result = eventService.getFeaturedEvents();
        
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        
        verify(eventRepository).findByFeaturedTrue();
    }
    
    @Test
    public void testGetRecentEvents() {
        when(eventRepository.findTop5ByOrderByDateTimeDesc()).thenReturn(Arrays.asList(event2, event1, event3));
        
        List<Event> result = eventService.getRecentEvents();
        
        assertEquals(3, result.size());
        assertEquals(event2, result.get(0)); // El más reciente primero
        
        verify(eventRepository).findTop5ByOrderByDateTimeDesc();
    }
    
    @Test
    public void testSearchEventsByKeyword() {
        when(eventRepository.searchEvents("FPS")).thenReturn(Arrays.asList(event1, event3));
        
        List<Event> result = eventService.searchEvents("FPS");
        
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        
        verify(eventRepository).searchEvents("FPS");
    }
    
    @Test
    public void testGetEventsByGameType() {
        when(eventRepository.findByGameType("FPS")).thenReturn(Arrays.asList(event1, event3));
        
        List<Event> result = eventService.getEventsByGameType("FPS");
        
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        
        verify(eventRepository).findByGameType("FPS");
    }
    
    @Test
    public void testGetEventsByLocation() {
        when(eventRepository.findByLocation("Location A")).thenReturn(Arrays.asList(event1, event3));
        
        List<Event> result = eventService.getEventsByLocation("Location A");
        
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        
        verify(eventRepository).findByLocation("Location A");
    }
    
    @Test
    public void testSearchEventsWithFilters() {
        List<Event> allEvents = Arrays.asList(event1, event2, event3);
        when(eventRepository.findAll()).thenReturn(allEvents);
        
        List<Event> resultKeyword = eventService.searchEvents("FPS", null, null);
        List<Event> resultGameType = eventService.searchEvents(null, "FPS", null);
        List<Event> resultLocation = eventService.searchEvents(null, null, "Location A");
        List<Event> resultCombined = eventService.searchEvents("FPS", "FPS", "Location A");
        
        assertEquals(1, resultKeyword.size());
        assertTrue(resultKeyword.contains(event3));
        
        assertEquals(2, resultGameType.size());
        assertTrue(resultGameType.contains(event1));
        assertTrue(resultGameType.contains(event3));
        
        assertEquals(2, resultLocation.size());
        assertTrue(resultLocation.contains(event1));
        assertTrue(resultLocation.contains(event3));
        
        assertEquals(1, resultCombined.size());
        assertTrue(resultCombined.contains(event3));
        
        verify(eventRepository, times(4)).findAll();
    }
    
    @Test
    public void testSaveEvent() {
        when(eventRepository.save(event1)).thenReturn(event1);
        
        Event result = eventService.saveEvent(event1);
        
        assertNotNull(result);
        assertEquals(event1.getId(), result.getId());
        assertEquals(event1.getName(), result.getName());
        
        verify(eventRepository).save(event1);
    }
    
    @Test
    public void testDeleteEvent() {
        doNothing().when(eventRepository).deleteById(1L);
        
        eventService.deleteEvent(1L);
        
        verify(eventRepository).deleteById(1L);
    }
}