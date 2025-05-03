package com.example.game_events.Controller;


import com.example.game_events.Controller.ApiAuthController;
import com.example.game_events.Model.AuthRequest;
import com.example.game_events.Model.AuthResponse;
import com.example.game_events.Model.User;
import com.example.game_events.Service.JwtService;
import com.example.game_events.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ApiAuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ApiAuthController apiAuthController;

    private AuthRequest authRequest;
    private Authentication authentication;
    private UserDetails userDetails;
    private User user;

    @BeforeEach
    public void setup() {
        // Configurar datos de prueba
        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    @Test
    public void testAuthenticateSuccess() {
        // Configurar mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(userDetails)).thenReturn("test-jwt-token");
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(user));

        // Ejecutar método bajo prueba
        ResponseEntity<AuthResponse> response = apiAuthController.authenticate(authRequest);

        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-jwt-token", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUsername());
    }
}