// src/main/java/com/example/game_events/Model/AuthRequest.java
package com.example.game_events.Model;

public class AuthRequest {
    private String username;
    private String password;

    // Constructores
    public AuthRequest() {}

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

