package com.example.game_events.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.game_events.Model.User;
import com.example.game_events.Service.UserService;
import com.example.game_events.Service.UserServiceImpl;

@Controller
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        
        // Añadir título para el layout
        model.addAttribute("title", "Register");
        
        // Especificar el fragmento de contenido
        model.addAttribute("content", "register :: content");
        
        // Retornar el layout
        return "layout";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists");
            model.addAttribute("user", user);
            
            // Añadir título para el layout
            model.addAttribute("title", "Register");
            
            // Especificar el fragmento de contenido
            model.addAttribute("content", "register :: content");
            
            return "layout";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            model.addAttribute("user", user);
            
            // Añadir título para el layout
            model.addAttribute("title", "Register");
            
            // Especificar el fragmento de contenido
            model.addAttribute("content", "register :: content");
            
            return "layout";
        }
        
        // Register the user
        userService.registerUser(user);
        
        return "redirect:/login?registered";
    }
    
    // Método adicional para la vista de login
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Añadir título para el layout
        model.addAttribute("title", "Login");
        
        // Especificar el fragmento de contenido
        model.addAttribute("content", "login :: content");
        
        // Retornar el layout
        return "layout";
    }
}