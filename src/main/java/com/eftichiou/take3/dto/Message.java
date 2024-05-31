package com.eftichiou.take3.dto;

import java.time.Instant;

public class Message {
    private String message;
    private String date;
    private int roomId;
    private int userId;
    private String firstName;
    private String lastName;

    @SuppressWarnings("unused")
    public Message() {
    }

    @SuppressWarnings("unused")
    public Message(String message, int roomId) {
        this.message = message;
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate() {
        this.date = Instant.now().toString();
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @SuppressWarnings("unused")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @SuppressWarnings("unused")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @SuppressWarnings("unused")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
