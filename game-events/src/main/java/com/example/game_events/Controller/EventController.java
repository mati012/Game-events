package com.example.game_events.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;
import com.example.game_events.Service.EventServiceImpl;

@Controller
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;
    
    @Autowired
    public EventController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping("")
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "events/list";
    }
    
    @GetMapping("/search")
    public String searchEvents(@RequestParam(name = "keyword", required = false) String keyword, 
                              @RequestParam(name = "gameType", required = false) String gameType,
                              @RequestParam(name = "location", required = false) String location,
                              Model model) {
        List<Event> searchResults;
        
        if (keyword != null && !keyword.isEmpty()) {
            searchResults = eventService.searchEvents(keyword);
        } else if (gameType != null && !gameType.isEmpty()) {
            searchResults = eventService.getEventsByGameType(gameType);
        } else if (location != null && !location.isEmpty()) {
            searchResults = eventService.getEventsByLocation(location);
        } else {
            searchResults = eventService.getAllEvents();
        }
        
        model.addAttribute("events", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("gameType", gameType);
        model.addAttribute("location", location);
        
        return "events/search";
    }
    
    @GetMapping("/{id}/details")
    public String eventDetails(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        model.addAttribute("event", event);
        return "events/details";
    }
}