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
        
        // Return the register template directly
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists");
            model.addAttribute("user", user);
            
            return "register";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            model.addAttribute("user", user);
            
            return "register";
        }
        
        // Register the user
        userService.registerUser(user);
        
        return "redirect:/login?registered";
    }
    
    // Método adicional para la vista de login
    @GetMapping("/login")
    public String showLoginForm() {
        // Return the login template directly
        return "login";
    }
}