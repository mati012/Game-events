package com.example.game_events.Service;

import java.util.List;
import java.util.Optional;
import com.example.game_events.Model.Event;

public interface EventService {

    List<Event> getAllEvents();

    Optional<Event> getEventById(Long id);

    List<Event> getFeaturedEvents();

    List<Event> getRecentEvents();

    List<Event> searchEvents(String keyword);

    List<Event> searchEvents(String keyword, String gameType, String location);

    List<Event> getEventsByGameType(String gameType);

    List<Event> getEventsByLocation(String location);

    Event saveEvent(Event event);

    void deleteEvent(Long id);
}