package com.eftichiou.take3.dao;

import java.util.List;

import com.corundumstudio.socketio.SocketIOClient;
import com.eftichiou.take3.dto.UserWithAuthentication;
import com.eftichiou.take3.dto.UserWithoutCredentials;
import com.eftichiou.take3.dto.requests.RoomIdAndPass;
import com.eftichiou.take3.entity.User;

public interface UserDao {
    List<User> findAll();

    UserWithoutCredentials findById(int id);

    void deleteById(int id);

    User findByEmail(String email);

    User addUser(User user);

    @SuppressWarnings("unused")
    User loginUser(User user);

    void joinRoom(RoomIdAndPass rpwe, User user) throws Exception;

    @SuppressWarnings("unused")
    void leaveRoom(int roomId, int userId);

    void leaveRoom(int roomId, User user);

    UserWithAuthentication findUserWithAuthStatus(SocketIOClient client) throws Exception;
}
