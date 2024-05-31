package com.eftichiou.take3.dto;

public class RoomWithAdminId {
    private String roomName;
    private String password;
    private int adminId;

    @SuppressWarnings("unused")
    public String getRoomName() {
        return roomName;
    }

    @SuppressWarnings("unused")
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("unused")
    public int getAdminId() {
        return adminId;
    }

    @SuppressWarnings("unused")
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "RoomWithAdminId [roomName=" + roomName + ", password=" + password + ", adminId=" + adminId + "]";
    }
}
