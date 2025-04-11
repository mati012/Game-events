package com.example.game_events.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventApiController {
    
    private final EventService eventService;
    
    @Autowired
    public EventApiController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping("")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "gameType", required = false) String gameType,
            @RequestParam(name = "location", required = false) String location) {
        
        List<Event> searchResults = eventService.searchEvents(keyword, gameType, location);
        return ResponseEntity.ok(searchResults);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return ResponseEntity.ok(event);
    }
}