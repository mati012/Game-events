package com.example.game_events.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.game_events.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
 
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
     @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    
}