package com.example.game_events.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.game_events.Model.Event;
import com.example.game_events.Repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> getFeaturedEvents() {
        return eventRepository.findByFeaturedTrue();
    }

    @Override
    public List<Event> getRecentEvents() {
        return eventRepository.findTop5ByOrderByDateTimeDesc();
    }

    @Override
    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchEvents(keyword);
    }

    @Override
    public List<Event> getEventsByGameType(String gameType) {
        return eventRepository.findByGameType(gameType);
    }

    @Override
    public List<Event> getEventsByLocation(String location) {
        return eventRepository.findByLocation(location);
    }

    @Override
    public List<Event> searchEvents(String keyword, String gameType, String location) {
        return eventRepository.findAll().stream()
                .filter(event -> (keyword == null || keyword.isEmpty() || 
                                  event.getName().contains(keyword) || 
                                  event.getDescription().contains(keyword)))
                .filter(event -> (gameType == null || gameType.isEmpty() || 
                                  event.getGameType().equalsIgnoreCase(gameType)))
                .filter(event -> (location == null || location.isEmpty() || 
                                  event.getLocation().equalsIgnoreCase(location)))
                .collect(Collectors.toList());
    }

    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}