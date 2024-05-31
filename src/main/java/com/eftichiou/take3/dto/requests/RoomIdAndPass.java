package com.eftichiou.take3.dto.requests;

public class RoomIdAndPass {

	
	private String password;
	private int roomId;

	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	@Override
	public String toString() {
		return "RoomPasswordWithEmail [password=" + password + ", roomId=" + roomId + "]";
	}

	

}
