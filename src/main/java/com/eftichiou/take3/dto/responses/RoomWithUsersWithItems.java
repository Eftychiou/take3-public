package com.eftichiou.take3.dto.responses;


import java.util.List;

import com.eftichiou.take3.dto.ItemWithUser;
import com.eftichiou.take3.dto.UserWithoutCredentials;


public class RoomWithUsersWithItems {

	private int roomId;
	private String roomName;
	private List<UserWithoutCredentials> users;
	private List<ItemWithUser> items;
	private String adminUsername;
	
	
	public RoomWithUsersWithItems() {

	}


	public RoomWithUsersWithItems(int roomId, String roomName, List<UserWithoutCredentials> users,List<ItemWithUser> items) {
		
		this.roomId = roomId;
		this.roomName = roomName;
		this.users = users;
		this.items = items;
	}


	public int getRoomId() {
		return roomId;
	}


	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}


	public String getRoomName() {
		return roomName;
	}


	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}


	public List<UserWithoutCredentials> getUsers() {
		return users;
	}


	public void setUsers(List<UserWithoutCredentials> users) {
		this.users = users;
	}


	public List<ItemWithUser> getItems() {
		return items;
	}


	public void setItems(List<ItemWithUser> items) {
		this.items = items;
	}


	public String getAdminUsername() {
		return adminUsername;
	}


	public void setAdminUsername(String adminUsername) {
		this.adminUsername = adminUsername;
	}


	


	
	
	
	
}
