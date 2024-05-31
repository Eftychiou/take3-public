package com.eftichiou.take3.entity;

import java.util.List;
import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "room_name")
    private String roomName;
    @Column(name = "password")
    private String password;
    @JsonIgnore
    @OneToOne(mappedBy = "room", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH})
    private User user;
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH})
    @JoinTable(name = "user_room", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns =
    @JoinColumn(name = "user_id"))
    private List<User> users;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    // This is unidirectional because the join-column is here and points to the room_id which is on
    private List<Item> items; // item table. Note that Item class has nothing linking here

    public List<Item> getItems() {
        return items;
    }

    @SuppressWarnings("unused")
    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<User> getUsers() {
        return users;
    }

    @SuppressWarnings("unused")
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SuppressWarnings("unused")
    public Room() {
    }

    public Room(String roomName, String password) {
        this.roomName = roomName;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    @SuppressWarnings("unused")
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Room [id=" + id + ", roomName=" + roomName + ", password=" + password + "]";
    }
}
