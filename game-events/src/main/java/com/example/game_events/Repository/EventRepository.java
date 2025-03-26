package com.example.game_events.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.game_events.Model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByFeaturedTrue();
    
    List<Event> findTop5ByOrderByDateTimeDesc();
    
    @Query("SELECT e FROM Event e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.gameType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchEvents(@Param("keyword") String keyword);
    
    List<Event> findByGameType(String gameType);
    
    List<Event> findByLocation(String location);
}