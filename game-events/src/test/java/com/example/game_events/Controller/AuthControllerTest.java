package com.example.game_events.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import com.example.game_events.Model.User;
import com.example.game_events.Service.UserService;

@SpringBootTest
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(model.addAttribute(anyString(), any())).thenReturn(model);
    }

    @Test
    public void testShowRegistrationForm() {
        // Act
        String viewName = authController.showRegistrationForm(model);

        // Assert
        assertEquals("register", viewName);
        verify(model, atLeastOnce()).addAttribute(eq("nonce"), any(String.class));
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    public void testRegisterUserSuccess() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.existsByEmail("test@example.com")).thenReturn(false);
        when(userService.registerUser(any(User.class))).thenReturn(user);

        // Act
        String viewName = authController.registerUser(user, model);

        // Assert
        assertEquals("redirect:/login?registered", viewName);
        verify(userService).existsByUsername("testuser");
        verify(userService).existsByEmail("test@example.com");
        verify(userService).registerUser(user);
    }

    @Test
    public void testRegisterUserUsernameExists() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // Act
        String viewName = authController.registerUser(user, model);

        // Assert
        assertEquals("register", viewName);
        verify(userService).existsByUsername("testuser");
        verify(userService, never()).existsByEmail(anyString());
        verify(userService, never()).registerUser(any(User.class));
        verify(model).addAttribute("usernameError", "El nombre de usuario ya existe");
        verify(model).addAttribute("user", user);
    }

    @Test
    public void testRegisterUserEmailExists() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        String viewName = authController.registerUser(user, model);

        // Assert
        assertEquals("register", viewName);
        verify(userService).existsByUsername("testuser");
        verify(userService).existsByEmail("test@example.com");
        verify(userService, never()).registerUser(any(User.class));
        verify(model).addAttribute("emailError", "El correo electrónico ya existe");
        verify(model).addAttribute("user", user);
    }

    @Test
    public void testShowLoginForm() {
        // Act
        String viewName = authController.showLoginForm(model);

        // Assert
        assertEquals("login", viewName);
        verify(model, atLeastOnce()).addAttribute(eq("nonce"), any(String.class));
    }
}