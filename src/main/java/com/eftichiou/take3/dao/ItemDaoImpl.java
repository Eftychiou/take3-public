package com.eftichiou.take3.dao;

import java.util.List;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eftichiou.take3.dto.UserWithoutCredentials;
import com.eftichiou.take3.dto.requests.RoomIdAndItemId;
import com.eftichiou.take3.entity.Item;
import com.eftichiou.take3.entity.Room;
import com.eftichiou.take3.entity.User;
import com.eftichiou.take3.exceptions.CustomException;
import com.eftichiou.take3.rest.Controllers;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final EntityManager entityManager;
    private static final Logger log = LoggerFactory.getLogger(Controllers.class);

    @Autowired
    public ItemDaoImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    @Transactional
    public Boolean addItem(String itemName, int roomId) {
        try {
            Item item = new Item(itemName);
            Room room = entityManager.find(Room.class, roomId);
            List<Item> itemList = room.getItems();
            itemList.add(item);
            return true;
        } catch (Exception exc) {
            log.debug(exc.getMessage());
            throw new CustomException("Room Already Exists or User already created a room(only " +
                    "one room per user)",
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<Item> findAllItemsOfRoom(int roomId) {
        Room room = entityManager.find(Room.class, roomId);
        return room.getItems();
    }

    @Override
    @Transactional
    public List<Item> deleteItem(RoomIdAndItemId roomIdAndItemId) {
        Room room = entityManager.find(Room.class, roomIdAndItemId.getRoomId());
        List<Item> items = room.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == roomIdAndItemId.getItemId()) {
                items.remove(i);
                break;
            }
        }
        return items;
    }

    @Override
    @Transactional
    public List<Item> toggleCarrier(RoomIdAndItemId roomIdAndItemId, int userId) {
        Room room = entityManager.find(Room.class, roomIdAndItemId.getRoomId());
        List<Item> items = room.getItems();
        for (Item item : items) {
            if (item.getId() == roomIdAndItemId.getItemId()) {
                User theUser = entityManager.find(User.class, userId);
                if (item.getUser() == theUser) {
                    item.setUser(null);
                    break;
                }
//				List<Item> usersItems = theUser.getItems();
//				usersItems.add(theItem);
                item.setUser(theUser);
                break;
            }
        }
        return items;
    }

    @Override
    public UserWithoutCredentials getUser(int itemId) {
        try {
            Item item = entityManager.find(Item.class, itemId);
            User user = item.getUser();
            return new UserWithoutCredentials(user.getId(),
                    user.getFirstName(), user.getLastName(), user.getAvatarImgUrl());
        } catch (Exception exc) {
            log.debug(exc.getMessage());
            throw new CustomException("CHECK ERROR", HttpStatus.NOT_FOUND);
        }
    }
}
