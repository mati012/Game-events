package com.example.game_events.Model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventTest {

    private Event event;
    private LocalDateTime testDateTime;
    private Set<User> participants;

    @BeforeEach
    public void setup() {
        testDateTime = LocalDateTime.now();
        participants = new HashSet<>();
        User user1 = new User("user1", "password1", "user1@example.com");
        user1.setId(1L);
        participants.add(user1);
        
        event = new Event();
    }

    @Test
    public void testEventEmptyConstructor() {
        assertNull(event.getId());
        assertNull(event.getName());
        assertNull(event.getDescription());
        assertNull(event.getLocation());
        assertNull(event.getDateTime());
        assertNull(event.getImageUrl());
        assertNull(event.getGameType());
        assertEquals(0, event.getMaxParticipants());
        assertEquals(0, event.getCurrentParticipants());
        assertFalse(event.isFeatured());
        assertNull(event.getParticipants());
    }

    @Test
    public void testEventParameterizedConstructor() {
        event = new Event("Test Event", "Test Description", "Test Location",
                         testDateTime, "test-image.jpg", "FPS", 20, true);
        
        assertEquals("Test Event", event.getName());
        assertEquals("Test Description", event.getDescription());
        assertEquals("Test Location", event.getLocation());
        assertEquals(testDateTime, event.getDateTime());
        assertEquals("test-image.jpg", event.getImageUrl());
        assertEquals("FPS", event.getGameType());
        assertEquals(20, event.getMaxParticipants());
        assertEquals(0, event.getCurrentParticipants());
        assertTrue(event.isFeatured());
    }

    @Test
    public void testEventSettersAndGetters() {
        event.setId(1L);
        event.setName("Updated Event");
        event.setDescription("Updated Description");
        event.setLocation("Updated Location");
        event.setDateTime(testDateTime);
        event.setImageUrl("updated-image.jpg");
        event.setGameType("RPG");
        event.setMaxParticipants(30);
        event.setCurrentParticipants(15);
        event.setFeatured(true);
        event.setParticipants(participants);
        
        assertEquals(1L, event.getId());
        assertEquals("Updated Event", event.getName());
        assertEquals("Updated Description", event.getDescription());
        assertEquals("Updated Location", event.getLocation());
        assertEquals(testDateTime, event.getDateTime());
        assertEquals("updated-image.jpg", event.getImageUrl());
        assertEquals("RPG", event.getGameType());
        assertEquals(30, event.getMaxParticipants());
        assertEquals(15, event.getCurrentParticipants());
        assertTrue(event.isFeatured());
        assertEquals(participants, event.getParticipants());
    }
}