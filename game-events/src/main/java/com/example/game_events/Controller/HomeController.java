package com.example.game_events.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.game_events.Service.EventService;
import com.example.game_events.Service.EventServiceImpl;

@Controller
public class HomeController {
    
    private final EventService eventService;
    
    @Autowired
    public HomeController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("featuredEvents", eventService.getFeaturedEvents());
        model.addAttribute("recentEvents", eventService.getRecentEvents());
        
        // Añadir título para el layout
        model.addAttribute("title", "Home");
        
        // Especificar el fragmento de contenido
        model.addAttribute("content", "home :: content");
        
        // Retornar el layout en lugar de la página específica
        return "layout";
    }
}