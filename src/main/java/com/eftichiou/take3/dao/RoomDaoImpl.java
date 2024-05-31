package com.eftichiou.take3.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.eftichiou.take3.dto.ItemWithUser;
import com.eftichiou.take3.dto.RoomWithoutPassword;
import com.eftichiou.take3.dto.UserWithoutCredentials;
import com.eftichiou.take3.dto.responses.RoomWithUsersWithItems;
import com.eftichiou.take3.dto.responses.RoomWithoutPassWithoutUsers;
import com.eftichiou.take3.entity.Item;
import com.eftichiou.take3.entity.Room;
import com.eftichiou.take3.entity.User;
import com.eftichiou.take3.exceptions.CustomException;
import com.eftichiou.take3.tools.Tools;

@Repository
public class RoomDaoImpl implements RoomDao {
    private final EntityManager entityManager;
    @SuppressWarnings("unused")
    @Value("${defaultItems}")
    private String defaultItemsString;

    @Autowired
    public RoomDaoImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    @Transactional
    public List<RoomWithoutPassWithoutUsers> addRoom(String roomName, String email, String roomPassword) throws Exception {
        Query findUser = entityManager.createQuery("from User u where u.email='" + email + "'");
        User user = (User) findUser.getSingleResult();
        if (user.getRoom() != null) throw new Exception("You already created one room");
        Room newRoom = new Room(roomName, roomPassword);
        List<Item> defaultItems = new ArrayList<>();
        Arrays.stream(defaultItemsString.split(",")).forEach(item -> defaultItems.add(new Item(item)));
        newRoom.setItems(defaultItems);
        newRoom.setUser(user);
        user.setRoom(newRoom);
        return findAllRoomsWithoutUsers();
    }

    @Override
    @Transactional
    public List<RoomWithoutPassWithoutUsers> deleteRoom(int id) {
        try {
            Room room = entityManager.find(Room.class, id);
            room.getUser().setRoom(null);
            entityManager.createQuery("DELETE FROM Room r where r.id='" + id + "'").executeUpdate();
            return findAllRoomsWithoutUsers();
        } catch (Exception exc) {
            throw new CustomException(exc);
        }
    }

    @Override
    public List<Room> findAll() {
        Session session = entityManager.unwrap(Session.class);
        return Tools.castList(Room.class, session.createQuery("from Room").getResultList());
    }

    @Override
    @Transactional
    public UserWithoutCredentials findRoomAdmin(int roomId) {
        Room room = entityManager.find(Room.class, roomId);
        User user = room.getUser();
        return new UserWithoutCredentials(user.getId(), user.getFirstName(), user.getLastName()
                , user.getAvatarImgUrl());
    }

    @Override
    public List<UserWithoutCredentials> findUsers(int roomId) {
        try {
            Session session = entityManager.unwrap(Session.class);
            Room theRoom = session.get(Room.class, roomId);
            List<User> usersList = theRoom.getUsers();
            List<UserWithoutCredentials> usersListWithoutCred = new ArrayList<>();
            for (User u : usersList) {
                UserWithoutCredentials uwc = new UserWithoutCredentials(u.getId(), u.getFirstName(), u.getLastName(),
                        u.getAvatarImgUrl());
                usersListWithoutCred.add(uwc);
            }
            return usersListWithoutCred;
        } catch (RuntimeException exc) {
            throw new CustomException("Something Went wrong", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<RoomWithoutPassword> findAllRoomsWithUsers() {
        Query allRoomQuery = entityManager.createQuery("from Room");
        List<Room> allRoomsWithPass = Tools.castList(Room.class, allRoomQuery.getResultList());
        List<RoomWithoutPassword> allRoomsWithoutPass = new ArrayList<>();
        for (Room r : allRoomsWithPass) {
            RoomWithoutPassword rwp = new RoomWithoutPassword();
            rwp.setId(r.getId());
            rwp.setRoomName(r.getRoomName());
            List<User> usersListWithPass = r.getUsers();
            List<UserWithoutCredentials> allUsersWithoutCred = new ArrayList<>();
            for (User u : usersListWithPass) {
                UserWithoutCredentials uwc = new UserWithoutCredentials(u.getId(), u.getFirstName(), u.getLastName(),
                        u.getAvatarImgUrl());
                allUsersWithoutCred.add(uwc);
            }
            rwp.setUsers(allUsersWithoutCred);
            allRoomsWithoutPass.add(rwp);
        }
        return allRoomsWithoutPass;
    }

    @Override
    public List<RoomWithoutPassWithoutUsers> findAllRoomsWithoutUsers() {
        Query allRoomQuery = entityManager.createQuery("from Room");
        List<Room> rooms = Tools.castList(Room.class, allRoomQuery.getResultList());
        List<RoomWithoutPassWithoutUsers> allRoomsWithoutPassWithoutUsers = new ArrayList<>();
        for (Room r : rooms) {
            RoomWithoutPassWithoutUsers rwpwu = new RoomWithoutPassWithoutUsers(r.getId(), r.getRoomName());
            allRoomsWithoutPassWithoutUsers.add(rwpwu);
        }
        return allRoomsWithoutPassWithoutUsers;
    }

    @Override
    @Transactional
    public RoomWithUsersWithItems calculateRoomChanges(int roomId) {
        Room room = entityManager.find(Room.class, roomId);
        List<UserWithoutCredentials> usersWC = new ArrayList<>();
        List<User> users = room.getUsers();
        for (User u : users) {
            UserWithoutCredentials uwc = new UserWithoutCredentials(u.getId(), u.getFirstName(), u.getLastName(),
                    u.getAvatarImgUrl());
            usersWC.add(uwc);
        }
        List<Item> items = room.getItems();
        List<ItemWithUser> itemsWithUser = new ArrayList<>();
        for (Item item : items) {
            ItemWithUser itemWithUser = new ItemWithUser(
                    item.getId(),
                    item.getItemName()
            );
            if (item.getUser() != null) {
                itemWithUser.setCarrier(new UserWithoutCredentials(item.getUser().getId(),
                        item.getUser().getFirstName(), item.getUser().getLastName(),
                        item.getUser().getAvatarImgUrl()));
            } else {
                itemWithUser.setCarrier(new UserWithoutCredentials());
            }
            itemsWithUser.add(itemWithUser);
        }
        RoomWithUsersWithItems newRoom = new RoomWithUsersWithItems(
                room.getId(),
                room.getRoomName(),
                usersWC,
                itemsWithUser
        );
        if (room.getUser() != null) newRoom.setAdminUsername(room.getUser().getEmail());
        return newRoom;
    }

    @Override
    @Transactional
    public void clearUserRoom() {
        Query allUsersQuery = entityManager.createQuery("from User");
        List<User> users = Tools.castList(User.class, allUsersQuery.getResultList());
        for (User u : users) {
            List<Room> rms = u.getRooms();
            rms.clear();
        }
    }
}
