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
        String viewName = authController.showRegistrationForm(model);

        assertEquals("register", viewName);
        verify(model, atLeastOnce()).addAttribute(eq("nonce"), any(String.class));
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    public void testRegisterUserSuccess() {
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.existsByEmail("test@example.com")).thenReturn(false);
        when(userService.registerUser(any(User.class))).thenReturn(user);

        String viewName = authController.registerUser(user, model);

        assertEquals("redirect:/login?registered", viewName);
        verify(userService).existsByUsername("testuser");
        verify(userService).existsByEmail("test@example.com");
        verify(userService).registerUser(user);
    }

    @Test
    public void testRegisterUserUsernameExists() {
        when(userService.existsByUsername("testuser")).thenReturn(true);

        
        String viewName = authController.registerUser(user, model);

        assertEquals("register", viewName);
        verify(userService).existsByUsername("testuser");
        verify(userService, never()).existsByEmail(anyString());
        verify(userService, never()).registerUser(any(User.class));
        verify(model).addAttribute("usernameError", "El nombre de usuario ya existe");
        verify(model).addAttribute("user", user);
    }

    @Test
    public void testRegisterUserEmailExists() {
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        String viewName = authController.registerUser(user, model);

        assertEquals("register", viewName);
        verify(userService).existsByUsername("testuser");
        verify(userService).existsByEmail("test@example.com");
        verify(userService, never()).registerUser(any(User.class));
        verify(model).addAttribute("emailError", "El correo electrónico ya existe");
        verify(model).addAttribute("user", user);
    }

    @Test
    public void testShowLoginForm() {
        String viewName = authController.showLoginForm(model);

        assertEquals("login", viewName);
        verify(model, atLeastOnce()).addAttribute(eq("nonce"), any(String.class));
    }
}