package com.eftichiou.take3.dto;

import com.eftichiou.take3.entity.User;

public class UserWithAuthentication {
    private Boolean isAuthenticated;
    private User user;

    public UserWithAuthentication(Boolean isAuthenticated, User user) {
        this.isAuthenticated = isAuthenticated;
        this.user = user;
    }

    @SuppressWarnings("unused")
    public UserWithAuthentication() {
    }

    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    @SuppressWarnings("unused")
    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
