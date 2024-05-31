package com.eftichiou.take3.dao;

import java.util.List;

import com.eftichiou.take3.dto.RoomWithoutPassword;
import com.eftichiou.take3.dto.UserWithoutCredentials;
import com.eftichiou.take3.dto.responses.RoomWithUsersWithItems;
import com.eftichiou.take3.dto.responses.RoomWithoutPassWithoutUsers;
import com.eftichiou.take3.entity.Room;

public interface RoomDao {
    List<RoomWithoutPassWithoutUsers> addRoom(String roomName, String email, String roomPassword) throws Exception;

    List<RoomWithoutPassWithoutUsers> deleteRoom(int id);

    @SuppressWarnings("unused")
    List<Room> findAll();

    UserWithoutCredentials findRoomAdmin(int roomId);

    List<UserWithoutCredentials> findUsers(int roomId);

    List<RoomWithoutPassword> findAllRoomsWithUsers();

    List<RoomWithoutPassWithoutUsers> findAllRoomsWithoutUsers();

    RoomWithUsersWithItems calculateRoomChanges(int roomId);

    void clearUserRoom();
}
