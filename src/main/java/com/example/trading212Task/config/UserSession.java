package com.example.trading212Task.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Component
public class UserSession {
    private long id;
    private String username;

    public void login(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getUserId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return id > 0;
    }

    public void logout() {
        id = 0;
        username = null;
    }

}
