package com.example.game_events.Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

    private User user;
    private Set<Event> events;
    private Set<Role> roles;

    @BeforeEach
    public void setup() {
        events = new HashSet<>();
        Event event1 = new Event();
        event1.setId(1L);
        event1.setName("Test Event");
        events.add(event1);
        
        roles = new HashSet<>();
        Role role1 = new Role("USER");
        role1.setId(1L);
        roles.add(role1);
        
        user = new User();
    }

    @Test
    public void testUserEmptyConstructor() {
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getProfileImageUrl());
        assertNull(user.getEvents());
        assertNull(user.getRoles());
    }

    @Test
    public void testUserParameterizedConstructor() {
        user = new User("testuser", "password", "test@example.com");
        
        assertNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertNull(user.getProfileImageUrl());
        assertNull(user.getEvents());
        assertNull(user.getRoles());
    }

    @Test
    public void testUserSettersAndGetters() {
        user.setId(1L);
        user.setUsername("updateduser");
        user.setPassword("updatedpassword");
        user.setEmail("updated@example.com");
        user.setProfileImageUrl("profile.jpg");
        user.setEvents(events);
        user.setRoles(roles);
        
        assertEquals(1L, user.getId());
        assertEquals("updateduser", user.getUsername());
        assertEquals("updatedpassword", user.getPassword());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("profile.jpg", user.getProfileImageUrl());
        assertEquals(events, user.getEvents());
        assertEquals(roles, user.getRoles());
    }
}