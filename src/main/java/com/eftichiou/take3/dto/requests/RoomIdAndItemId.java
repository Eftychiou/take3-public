package com.eftichiou.take3.dto.requests;

public class RoomIdAndItemId {
    private int roomId;
    private int itemId;

    @SuppressWarnings("unused")
    public RoomIdAndItemId() {
    }

    @SuppressWarnings("unused")
    public RoomIdAndItemId(int roomId, int itemId) {
        this.roomId = roomId;
        this.itemId = itemId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getItemId() {
        return itemId;
    }

    @SuppressWarnings("unused")
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "RoomIdAndItemId [roomId=" + roomId + ", itemId=" + itemId + "]";
    }
}
