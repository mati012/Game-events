package com.example.game_events.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.game_events.Model.Role;
import com.example.game_events.Model.User;
import com.example.game_events.Repository.RoleRepository;
import com.example.game_events.Repository.UserRepository;

public class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    
    private User user1, user2;
    private Role userRole, adminRole;
    
    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        roleRepository = mock(RoleRepository.class);
        
        userService = new UserServiceImpl(userRepository, passwordEncoder, roleRepository);
        
        // Crear roles
        userRole = new Role("USER");
        userRole.setId(1L);
        
        adminRole = new Role("ADMIN");
        adminRole.setId(2L);
        
        // Crear roles para usuarios
        Set<Role> roles1 = new HashSet<>();
        roles1.add(userRole);
        
        Set<Role> roles2 = new HashSet<>();
        roles2.add(userRole);
        roles2.add(adminRole);
        
        // Crear usuarios
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("encodedPassword1");
        user1.setEmail("user1@example.com");
        user1.setRoles(roles1);
        
        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("encodedPassword2");
        user2.setEmail("user2@example.com");
        user2.setRoles(roles2);
    }
    
    @Test
    public void testGetAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        
        // Act
        List<User> result = userService.getAllUsers();
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        
        verify(userRepository).findAll();
    }
    
    @Test
    public void testGetUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        
        // Act
        Optional<User> foundUser = userService.getUserById(1L);
        Optional<User> notFoundUser = userService.getUserById(99L);
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("user1", foundUser.get().getUsername());
        assertFalse(notFoundUser.isPresent());
        
        verify(userRepository).findById(1L);
        verify(userRepository).findById(99L);
    }
    
    @Test
    public void testGetUserByUsername() {
        // Arrange
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        // Act
        Optional<User> foundUser = userService.getUserByUsername("user1");
        Optional<User> notFoundUser = userService.getUserByUsername("nonexistent");
        
        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("user1", foundUser.get().getUsername());
        assertFalse(notFoundUser.isPresent());
        
        verify(userRepository).findByUsername("user1");
        verify(userRepository).findByUsername("nonexistent");
    }
    
    @Test
    public void testExistsByUsername() {
        // Arrange
        when(userRepository.existsByUsername("user1")).thenReturn(true);
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);
        
        // Act
        boolean exists = userService.existsByUsername("user1");
        boolean notExists = userService.existsByUsername("nonexistent");
        
        // Assert
        assertTrue(exists);
        assertFalse(notExists);
        
        verify(userRepository).existsByUsername("user1");
        verify(userRepository).existsByUsername("nonexistent");
    }
    
    @Test
    public void testExistsByEmail() {
        // Arrange
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);
        
        // Act
        boolean exists = userService.existsByEmail("user1@example.com");
        boolean notExists = userService.existsByEmail("nonexistent@example.com");
        
        // Assert
        assertTrue(exists);
        assertFalse(notExists);
        
        verify(userRepository).existsByEmail("user1@example.com");
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }
    
    @Test
    public void testRegisterUserSuccess() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(3L);
            return savedUser;
        });
        
        // Act
        User result = userService.registerUser(newUser);
        
        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("newuser@example.com", result.getEmail());
        
        assertNotNull(result.getRoles());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains(userRole));
        
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByName("USER");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    public void testRegisterUserCreateNewRole() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty()); // No existe el rol
        when(roleRepository.save(any(Role.class))).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(3L);
            return savedUser;
        });
        
        // Act
        User result = userService.registerUser(newUser);
        
        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertNotNull(result.getRoles());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains(userRole));
        
        verify(roleRepository).findByName("USER");
        verify(roleRepository).save(any(Role.class));
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    public void testRegisterUserUsernameExists() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("user1"); // Ya existe
        newUser.setPassword("password");
        newUser.setEmail("new@example.com");
        
        when(userRepository.existsByUsername("user1")).thenReturn(true);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(newUser);
        });
        
        assertEquals("El nombre de usuario ya existe: user1", exception.getMessage());
        
        verify(userRepository).existsByUsername("user1");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testRegisterUserEmailExists() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("user1@example.com"); // Ya existe
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(true);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(newUser);
        });
        
        assertEquals("El correo electrónico ya existe: user1@example.com", exception.getMessage());
        
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("user1@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testUpdateUserSuccess() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("newpassword");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRoles(new HashSet<>(Arrays.asList(adminRole)));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        User result = userService.updateUser(updatedUser);
        
        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("newEncodedPassword", result.getPassword());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains(adminRole));
        
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(updatedUser);
    }
    
    @Test
    public void testUpdateUserNoPasswordChange() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("user1");
        existingUser.setPassword("$2a$10$encodedPassword");
        existingUser.setEmail("user1@example.com");
        existingUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("$2a$10$encodedPassword"); // Misma contraseña ya codificada
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRoles(new HashSet<>(Arrays.asList(adminRole)));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        User result = userService.updateUser(updatedUser);
        
        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("$2a$10$encodedPassword", result.getPassword()); // No cambia
        assertEquals("updated@example.com", result.getEmail());
        
        verify(userRepository).findById(1L);
        verify(passwordEncoder, never()).encode(anyString()); // No se codifica la contraseña
        verify(userRepository).save(updatedUser);
    }
    
    @Test
    public void testUpdateUserPreserveRoles() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("user1");
        existingUser.setPassword("oldEncodedPassword");
        existingUser.setEmail("user1@example.com");
        existingUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("newpassword");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRoles(null); // Sin roles
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        User result = userService.updateUser(updatedUser);
        
        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("newEncodedPassword", result.getPassword());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals(existingUser.getRoles(), result.getRoles()); // Se preservan los roles
        
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(updatedUser);
    }
    
    @Test
    public void testUpdateUserNoId() {
        // Arrange
        User invalidUser = new User();
        invalidUser.setUsername("invalid");
        invalidUser.setPassword("password");
        invalidUser.setEmail("invalid@example.com");
        // Sin ID
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(invalidUser);
        });
        
        assertEquals("El ID del usuario no puede ser nulo para la actualización.", exception.getMessage());
        
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testUpdateUserUserNotFound() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setId(99L); // ID que no existe
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("newpassword");
        updatedUser.setEmail("updated@example.com");
        
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        
        // Act
        User result = userService.updateUser(updatedUser);
        
        // Assert
        assertNotNull(result);
        assertEquals(updatedUser, result);
        
        verify(userRepository).findById(99L);
        verify(userRepository).save(updatedUser);
    }
    
    @Test
    public void testDeleteUser() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);
        
        // Act
        userService.deleteUser(1L);
        
        // Assert
        verify(userRepository).deleteById(1L);
    }
    
    @Test
    public void testRegisterUserWithNullRoles() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        newUser.setRoles(null); // Roles nulos
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(3L);
            return savedUser;
        });
        
        // Act
        User result = userService.registerUser(newUser);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getRoles());
        assertEquals(1, result.getRoles().size());
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    public void testUpdateUserEmptyRoles() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("user1");
        existingUser.setPassword("oldEncodedPassword");
        existingUser.setEmail("user1@example.com");
        existingUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("newpassword");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRoles(new HashSet<>()); // Roles vacíos
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        User result = userService.updateUser(updatedUser);
        
        // Assert
        assertNotNull(result);
        assertEquals(existingUser.getRoles(), result.getRoles()); // Se preservan los roles
        
        verify(userRepository).findById(1L);
        verify(userRepository).save(updatedUser);
    }
    
    @Test
    public void testRegisterUserWithExistingRoles() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        
        Set<Role> existingRoles = new HashSet<>();
        existingRoles.add(adminRole);
        newUser.setRoles(existingRoles);
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(3L);
            return savedUser;
        });
        
        // Act
        User result = userService.registerUser(newUser);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(adminRole));
        assertTrue(result.getRoles().contains(userRole));
        
        verify(userRepository).save(any(User.class));
    }
}