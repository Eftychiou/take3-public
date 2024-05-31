package com.eftichiou.take3.dto.requests;

@SuppressWarnings("unused")
public class UserEmailRoomId {
    private String userEmail;
    private int roomId;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "UserEmailRoomId [userEmail=" + userEmail + ", roomId=" + roomId + "]";
    }
}
