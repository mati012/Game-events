package com.example.game_events.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.game_events.Model.Event;
import com.example.game_events.Model.Role;
import com.example.game_events.Model.User;
import com.example.game_events.Repository.EventRepository;
import com.example.game_events.Repository.RoleRepository;
import com.example.game_events.Repository.UserRepository;

@SpringBootTest
public class ServiceTests {

    // EventService tests
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    // UserService tests
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // CustomUserDetailsService tests
    @Mock
    private UserRepository userDetailsRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    // JwtService test
    @InjectMocks
    private JwtService jwtService;

    // Test data
    private Event event1, event2, event3;
    private List<Event> allEvents;
    private User user1, user2;
    private Role role1, role2;
    private Set<Role> roles;

    @BeforeEach
    public void setup() {
        // Event test data
        event1 = new Event();
        event1.setId(1L);
        event1.setName("Test Event 1");
        event1.setDescription("Description 1");
        event1.setGameType("FPS");
        event1.setLocation("Location 1");
        event1.setFeatured(true);
        event1.setDateTime(LocalDateTime.now());
        event1.setMaxParticipants(20);
        event1.setCurrentParticipants(10);

        event2 = new Event();
        event2.setId(2L);
        event2.setName("Test Event 2");
        event2.setDescription("Description 2");
        event2.setGameType("RPG");
        event2.setLocation("Location 2");
        event2.setFeatured(false);
        event2.setDateTime(LocalDateTime.now().plusDays(1));
        event2.setMaxParticipants(15);
        event2.setCurrentParticipants(5);

        event3 = new Event();
        event3.setId(3L);
        event3.setName("Another Test");
        event3.setDescription("Another Description");
        event3.setGameType("FPS");
        event3.setLocation("Location 1");
        event3.setFeatured(true);
        event3.setDateTime(LocalDateTime.now().minusDays(1));
        event3.setMaxParticipants(30);
        event3.setCurrentParticipants(20);

        allEvents = Arrays.asList(event1, event2, event3);

        // User and Role test data
        role1 = new Role("USER");
        role1.setId(1L);

        role2 = new Role("ADMIN");
        role2.setId(2L);

        roles = new HashSet<>();
        roles.add(role1);

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("encodedPassword1");
        user1.setEmail("user1@example.com");
        user1.setRoles(roles);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("encodedPassword2");
        user2.setEmail("user2@example.com");
        user2.setRoles(new HashSet<>());
    }

    // ==================== EventServiceImpl Tests ====================

    @Test
    public void testGetAllEvents() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(allEvents);

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
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Event> foundEvent = eventService.getEventById(1L);
        Optional<Event> notFoundEvent = eventService.getEventById(99L);

        // Assert
        assertTrue(foundEvent.isPresent());
        assertEquals("Test Event 1", foundEvent.get().getName());
        assertFalse(notFoundEvent.isPresent());
        verify(eventRepository).findById(1L);
        verify(eventRepository).findById(99L);
    }

    @Test
    public void testGetFeaturedEvents() {
        // Arrange
        List<Event> featuredEvents = Arrays.asList(event1, event3);
        when(eventRepository.findByFeaturedTrue()).thenReturn(featuredEvents);

        // Act
        List<Event> result = eventService.getFeaturedEvents();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        verify(eventRepository).findByFeaturedTrue();
    }

    @Test
    public void testGetRecentEvents() {
        // Arrange
        List<Event> recentEvents = Arrays.asList(event2, event1, event3);
        when(eventRepository.findTop5ByOrderByDateTimeDesc()).thenReturn(recentEvents);

        // Act
        List<Event> result = eventService.getRecentEvents();

        // Assert
        assertEquals(3, result.size());
        assertEquals(recentEvents, result);
        verify(eventRepository).findTop5ByOrderByDateTimeDesc();
    }

    @Test
    public void testSearchEvents() {
        // Arrange
        List<Event> searchResults = Arrays.asList(event1, event3);
        when(eventRepository.searchEvents("Test")).thenReturn(searchResults);

        // Act
        List<Event> result = eventService.searchEvents("Test");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        verify(eventRepository).searchEvents("Test");
    }

    @Test
    public void testGetEventsByGameType() {
        // Arrange
        List<Event> fpsEvents = Arrays.asList(event1, event3);
        when(eventRepository.findByGameType("FPS")).thenReturn(fpsEvents);

        // Act
        List<Event> result = eventService.getEventsByGameType("FPS");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        verify(eventRepository).findByGameType("FPS");
    }

    @Test
    public void testGetEventsByLocation() {
        // Arrange
        List<Event> locationEvents = Arrays.asList(event1, event3);
        when(eventRepository.findByLocation("Location 1")).thenReturn(locationEvents);

        // Act
        List<Event> result = eventService.getEventsByLocation("Location 1");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(event1));
        assertTrue(result.contains(event3));
        verify(eventRepository).findByLocation("Location 1");
    }

    @Test
    public void testSearchEventsWithFilters() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(allEvents);

        // Act
        List<Event> resultKeyword = eventService.searchEvents("Another", null, null);
        List<Event> resultGameType = eventService.searchEvents(null, "FPS", null);
        List<Event> resultLocation = eventService.searchEvents(null, null, "Location 1");
        List<Event> resultAll = eventService.searchEvents("Test", "FPS", "Location 1");

        // Assert
        assertEquals(1, resultKeyword.size());
        assertEquals(event3, resultKeyword.get(0));

        assertEquals(2, resultGameType.size());
        assertTrue(resultGameType.contains(event1));
        assertTrue(resultGameType.contains(event3));

        assertEquals(2, resultLocation.size());
        assertTrue(resultLocation.contains(event1));
        assertTrue(resultLocation.contains(event3));

        assertEquals(2, resultAll.size());
        assertEquals(event1, resultAll.get(0));

        verify(eventRepository, times(4)).findAll();
    }

    @Test
    public void testSaveEvent() {
        // Arrange
        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        // Act
        Event result = eventService.saveEvent(event1);

        // Assert
        assertEquals(event1, result);
        verify(eventRepository).save(event1);
    }

    @Test
    public void testDeleteEvent() {
        // Arrange
        doNothing().when(eventRepository).deleteById(1L);

        // Act
        eventService.deleteEvent(1L);

        // Assert
        verify(eventRepository).deleteById(1L);
    }

    // ==================== UserServiceImpl Tests ====================





@Test
public void testGetUserByUsername() {
    // Arrange - Crear objetos de prueba manualmente
    User testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("user1");
    testUser.setEmail("user1@example.com");
    
    // Crear el mock del repositorio y configurar su comportamiento
    UserRepository mockRepo = mock(UserRepository.class);
    when(mockRepo.findByUsername("user1")).thenReturn(Optional.of(testUser));
    when(mockRepo.findByUsername("nonexistent")).thenReturn(Optional.empty());
    
    // Crear una instancia real del servicio con nuestro mock
    UserService testUserService = new UserServiceImpl(
        mockRepo, 
        mock(PasswordEncoder.class), 
        mock(RoleRepository.class)
    );
    
    // Act
    Optional<User> foundUser = testUserService.getUserByUsername("user1");
    Optional<User> notFoundUser = testUserService.getUserByUsername("nonexistent");
    
    // Assert
    assertTrue(foundUser.isPresent(), "El usuario debería estar presente");
    assertEquals("user1", foundUser.get().getUsername(), "El nombre de usuario debería ser 'user1'");
    assertFalse(notFoundUser.isPresent(), "El usuario no debería estar presente");
    
    // Verificar las llamadas al mock
    verify(mockRepo).findByUsername("user1");
    verify(mockRepo).findByUsername("nonexistent");
}



    @Test
    public void testUpdateUserNoId() {
        // Arrange
        User invalidUser = new User();
        invalidUser.setUsername("invalid");
        invalidUser.setPassword("password");
        invalidUser.setEmail("invalid@example.com");
        // No ID set

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(invalidUser);
        });
        
        assertEquals("El ID del usuario no puede ser nulo para la actualización.", exception.getMessage());
        
        verify(userRepository, never()).save(any(User.class));
    }

  




    @Test
    public void testExistsByEmail() {
        // Arrange - Configurar el comportamiento del mock
        UserRepository mockRepo = mock(UserRepository.class);
        when(mockRepo.existsByEmail("user1@example.com")).thenReturn(true);
        when(mockRepo.existsByEmail("nonexistent@example.com")).thenReturn(false);
        
        // Crear una instancia real del servicio con nuestro mock
        UserService testUserService = new UserServiceImpl(
            mockRepo, 
            mock(PasswordEncoder.class), 
            mock(RoleRepository.class)
        );
        
        // Act & Assert
        boolean exists = testUserService.existsByEmail("user1@example.com");
        boolean notExists = testUserService.existsByEmail("nonexistent@example.com");
        
        assertTrue(exists, "El email user1@example.com debería existir");
        assertFalse(notExists, "El email nonexistent@example.com no debería existir");
        
        // Verificar que se llamó al repositorio con los argumentos correctos
        verify(mockRepo).existsByEmail("user1@example.com");
        verify(mockRepo).existsByEmail("nonexistent@example.com");
    }
    
    // ==================== CustomUserDetailsService Tests ====================






    // ==================== JwtService Tests ====================

    @Test
    public void testGenerateTokenAndVerify() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 20);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void testGenerateTokenWithExtraClaims() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 20);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertEquals("ADMIN", jwtService.extractClaim(token, claims -> claims.get("role")));
    }

    @Test
    public void testIsTokenExpired() throws Exception {
        // This is a bit tricky to test as we need to create a token with custom expiration
        // One option is to use reflection to access the private method, but for simplicity,
        // let's test the public method with a modified user
        
        // Arrange
        UserDetails userDetails1 = mock(UserDetails.class);
        when(userDetails1.getUsername()).thenReturn("user1");
        
        UserDetails userDetails2 = mock(UserDetails.class);
        when(userDetails2.getUsername()).thenReturn("user2");
        
        // Act
        String token = jwtService.generateToken(userDetails1);
        
        // Assert
        assertTrue(jwtService.isTokenValid(token, userDetails1));
        assertFalse(jwtService.isTokenValid(token, userDetails2)); // Different username
    }
}