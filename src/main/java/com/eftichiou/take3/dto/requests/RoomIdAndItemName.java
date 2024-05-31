package com.eftichiou.take3.dto.requests;

public class RoomIdAndItemName {
    private int roomId;
    private String itemName;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getItemName() {
        return itemName;
    }

    @SuppressWarnings("unused")
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "RoomIdAndItemName [roomId=" + roomId + ", itemName=" + itemName + "]";
    }
}
