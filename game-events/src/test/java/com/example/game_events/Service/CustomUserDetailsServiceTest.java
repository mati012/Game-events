package com.example.game_events.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.game_events.Model.Role;
import com.example.game_events.Model.User;
import com.example.game_events.Repository.UserRepository;

public class CustomUserDetailsServiceTest {

    private CustomUserDetailsService userDetailsService;
    private UserRepository userRepository;
    private User testUser;
    private Set<Role> roles;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
        
        roles = new HashSet<>();
        Role userRole = new Role("USER");
        userRole.setId(1L);
        roles.add(userRole);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setRoles(roles);
    }

    @Test
    public void testLoadUserByUsernameSuccess() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        
        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        
        boolean hasUserRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_USER"));
        assertTrue(hasUserRole);
        
        verify(userRepository).findByUsername("testuser");
    }
    
    @Test
    public void testLoadUserByUsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        });
        
        assertEquals("Usuario no encontrado: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }
    
    @Test
    public void testLoadUserByUsernameNoPassword() {
        // Arrange
        User userNoPassword = new User();
        userNoPassword.setUsername("nopassword");
        userNoPassword.setPassword(""); // Contraseña vacía
        userNoPassword.setRoles(roles);
        
        when(userRepository.findByUsername("nopassword")).thenReturn(Optional.of(userNoPassword));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            userDetailsService.loadUserByUsername("nopassword");
        });
        
        assertEquals("El usuario no tiene contraseña configurada.", exception.getMessage());
        verify(userRepository).findByUsername("nopassword");
    }
    
    @Test
    public void testLoadUserByUsernameNoRoles() {
        // Arrange
        User userNoRoles = new User();
        userNoRoles.setUsername("noroles");
        userNoRoles.setPassword("password");
        userNoRoles.setRoles(null); // Sin roles
        
        when(userRepository.findByUsername("noroles")).thenReturn(Optional.of(userNoRoles));
        
        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("noroles");
        
        // Assert
        assertNotNull(userDetails);
        
        boolean hasDefaultUserRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_USER"));
        assertTrue(hasDefaultUserRole);
        
        verify(userRepository).findByUsername("noroles");
    }
    
    @Test
    public void testLoadUserByUsernameEmptyRoles() {
        // Arrange
        User userEmptyRoles = new User();
        userEmptyRoles.setUsername("emptyroles");
        userEmptyRoles.setPassword("password");
        userEmptyRoles.setRoles(new HashSet<>()); // Conjunto vacío de roles
        
        when(userRepository.findByUsername("emptyroles")).thenReturn(Optional.of(userEmptyRoles));
        
        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("emptyroles");
        
        // Assert
        assertNotNull(userDetails);
        
        boolean hasDefaultUserRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_USER"));
        assertTrue(hasDefaultUserRole);
        
        verify(userRepository).findByUsername("emptyroles");
    }
}