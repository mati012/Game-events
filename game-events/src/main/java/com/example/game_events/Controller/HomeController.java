package com.example.game_events.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.game_events.Service.EventService;

import java.util.UUID;

@Controller
public class HomeController {
    
    private final EventService eventService;
    
    @Autowired
    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping({"/", "/home", "/Events"})
    public String home(Model model) {
        String nonce = UUID.randomUUID().toString();
        
        model.addAttribute("featuredEvents", eventService.getFeaturedEvents());
        model.addAttribute("recentEvents", eventService.getRecentEvents());
        model.addAttribute("nonce", nonce);
        
        return "home";
    }
}