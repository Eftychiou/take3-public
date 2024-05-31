package com.eftichiou.take3.dto;

public class UserWithoutCredentials {
    private int id;
    private String firstName;
    private String lastName;
    private String avatarImgUrl;

    public UserWithoutCredentials() {
        this.id = 0;
        this.firstName = "";
        this.lastName = "";
        this.avatarImgUrl = "";
    }

    public UserWithoutCredentials(int id, String firstName, String lastName, String avatarImgUrl) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarImgUrl = avatarImgUrl;
    }

    @SuppressWarnings("unused")
    public String getFirstName() {
        return firstName;
    }

    @SuppressWarnings("unused")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @SuppressWarnings("unused")
    public String getLastName() {
        return lastName;
    }

    @SuppressWarnings("unused")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getAvatarImgUrl() {
        return avatarImgUrl;
    }

    @SuppressWarnings("unused")
    public void setAvatarImgUrl(String avatarImgUrl) {
        this.avatarImgUrl = avatarImgUrl;
    }

    @Override
    public String toString() {
        return "UserWithoutCredentials [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }
}
