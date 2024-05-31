package com.eftichiou.take3.dao;

import java.util.List;

import com.eftichiou.take3.dto.UserWithoutCredentials;
import com.eftichiou.take3.dto.requests.RoomIdAndItemId;
import com.eftichiou.take3.entity.Item;

public interface ItemDao {
    Boolean addItem(String itemName, int roomId);

    List<Item> findAllItemsOfRoom(int roomId);

    List<Item> deleteItem(RoomIdAndItemId roomIdAndItemId);

    List<Item> toggleCarrier(RoomIdAndItemId roomIdAndItemId, int userId);

    UserWithoutCredentials getUser(int itemId);
}
