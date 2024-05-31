package com.eftichiou.take3.dto.responses;

public class RoomWithoutPassWithoutUsers {

	private int id;
	private String roomName;

	public RoomWithoutPassWithoutUsers() {

	}

	public RoomWithoutPassWithoutUsers(int id, String roomName) {
		this.id = id;
		this.roomName = roomName;

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

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	@Override
	public String toString() {
		return "RoomWithoutPassWithoutUsers [id=" + id + ", roomName=" + roomName + "]";
	}

}
