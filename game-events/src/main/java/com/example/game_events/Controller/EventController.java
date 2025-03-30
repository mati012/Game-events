package com.example.game_events.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.game_events.Model.Event;
import com.example.game_events.Service.EventService;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("")
    public String listEvents(Model model) {
        String nonce = UUID.randomUUID().toString();

        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("nonce", nonce);

        return "events/list"; 
    }

    @GetMapping("/search")
    public String searchEvents(@RequestParam(name = "keyword", required = false) String keyword,
                                @RequestParam(name = "gameType", required = false) String gameType,
                                @RequestParam(name = "location", required = false) String location,
                                Model model) {
        String nonce = UUID.randomUUID().toString();

        List<Event> searchResults = eventService.searchEvents(keyword, gameType, location);

        model.addAttribute("events", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("gameType", gameType);
        model.addAttribute("location", location);
        model.addAttribute("nonce", nonce);

        return "events/search"; 
    }

    @GetMapping("/{id}/details")
    public String eventDetails(@PathVariable Long id, Model model) {
        String nonce = UUID.randomUUID().toString();

        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        model.addAttribute("event", event);
        model.addAttribute("nonce", nonce);

        return "events/details";
    }
}