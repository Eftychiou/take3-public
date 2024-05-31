package com.eftichiou.take3.dto;

import java.util.List;

public class RoomWithoutPassword {
    private int id;
    private String roomName;
    private List<UserWithoutCredentials> users;

    public RoomWithoutPassword() {
    }

    @SuppressWarnings("unused")
    public RoomWithoutPassword(int id, String roomName, List<UserWithoutCredentials> users) {
        this.id = id;
        this.roomName = roomName;
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @SuppressWarnings("unused")
    public List<UserWithoutCredentials> getUsers() {
        return users;
    }

    public void setUsers(List<UserWithoutCredentials> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "RoomWithoutPassword [id=" + id + ", roomName=" + roomName + ", users="
                + users + "]";
    }
}
