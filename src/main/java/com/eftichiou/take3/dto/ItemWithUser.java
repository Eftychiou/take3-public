package com.eftichiou.take3.dto;

public class ItemWithUser {
    private int id;
    private String itemName;
    UserWithoutCredentials carrier;

    @SuppressWarnings("unused")
    public ItemWithUser() {
    }

    public ItemWithUser(int id, String itemName) {
        this.id = id;
        this.itemName = itemName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getItemName() {
        return itemName;
    }

    @SuppressWarnings("unused")
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @SuppressWarnings("unused")
    public UserWithoutCredentials getCarrier() {
        return carrier;
    }

    public void setCarrier(UserWithoutCredentials carrier) {
        this.carrier = carrier;
    }

    @Override
    public String toString() {
        return "ItemWithUser [id=" + id + ", itemName=" + itemName + ", carrier=" + carrier + "]";
    }
}
