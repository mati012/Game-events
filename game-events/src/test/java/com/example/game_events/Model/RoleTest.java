package com.example.game_events.Model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoleTest {

    private Role role;
    private Set<User> users;

    @BeforeEach
    public void setup() {
        users = new HashSet<>();
        User user1 = new User("user1", "password1", "user1@example.com");
        user1.setId(1L);
        users.add(user1);
        
        role = new Role();
    }

    @Test
    public void testRoleEmptyConstructor() {
        assertNull(role.getId());
        assertNull(role.getName());
        assertNull(role.getUsers());
    }

    @Test
    public void testRoleParameterizedConstructor() {
        role = new Role("ADMIN");
        
        assertNull(role.getId());
        assertEquals("ADMIN", role.getName());
        assertNull(role.getUsers());
    }

    @Test
    public void testRoleSettersAndGetters() {
        role.setId(1L);
        role.setName("USER");
        role.setUsers(users);
        
        assertEquals(1L, role.getId());
        assertEquals("USER", role.getName());
        assertEquals(users, role.getUsers());
    }
}