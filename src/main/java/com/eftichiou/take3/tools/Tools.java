package com.eftichiou.take3.tools;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.eftichiou.take3.dao.UserDao;
import com.eftichiou.take3.dto.Message;
import com.eftichiou.take3.dto.responses.RoomWithUsersWithItems;
import com.eftichiou.take3.dto.responses.RoomWithoutPassWithoutUsers;
import com.eftichiou.take3.entity.User;

public class Tools {

	@Autowired
	private static UserDao userDAO;

	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
		List<T> r = new ArrayList<T>(c.size());
		for (Object o : c)
			r.add(clazz.cast(o));
		return r;
	}

	public static User validateAccTokenAndReturnUser(String access_token) throws Exception {
		try {
			if (access_token != null && access_token.startsWith("Bearer ")) {
				String token = access_token.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(token);
				String email = decodedJWT.getSubject(); // This will give the username that comes with the token
				return userDAO.findByEmail(email);

			} else {
				throw new Exception("Validation Failed");
			}

		} catch (Exception exc) {
			throw exc;
		}

	}

	public static Boolean valideToken(String access_token) {
		try {
			if (access_token != null && access_token.startsWith("Bearer ")) {
				String token = access_token.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				verifier.verify(token);
				return true;
			} else {
				return false;
			}

		} catch (Exception exc) {
			throw exc;
		}
	}

	public static Boolean validateClientsToken(SocketIOClient client)  {
		try {
			HandshakeData handshakeData = client.getHandshakeData();
			String access_token = handshakeData.getSingleUrlParam("access_token");
			if (access_token != null && access_token.startsWith("Bearer ")) {
				String token = access_token.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				verifier.verify(token);
				return true;
			} else {
				throw new Exception("Unauthenticated");
			}
		} catch (Exception exc) {
			client.sendEvent("error", "AUTHENTICATION_FAILED", exc.getMessage());
			client.disconnect();
			return false;
		}

	}
	
	public static void broadcastChanges(SocketIONamespace namespace, String roomId,String listener,RoomWithUsersWithItems changes) {
		Collection<SocketIOClient> clients = namespace.getAllClients();				
		for (SocketIOClient s : clients) {
			Set<String> rooms = s.getAllRooms();
			if (rooms.contains(roomId ))s.sendEvent(listener, changes);
		}	
	}
	public static void broadcastChanges(SocketIONamespace namespace, String roomId,String listener,List<RoomWithoutPassWithoutUsers> changes) {
		Collection<SocketIOClient> clients = namespace.getAllClients();				
		for (SocketIOClient s : clients) {
			Set<String> rooms = s.getAllRooms();
			if (rooms.contains(roomId))s.sendEvent("onRoomUpdates", false);
			s.sendEvent("roomListChanges", changes);	
			s.leaveRoom(roomId);
		}	
	}
	public static void broadcastMessage(SocketIONamespace namespace, String roomId,String listener,Message message) {
		Collection<SocketIOClient> clients = namespace.getAllClients();	
		for (SocketIOClient s : clients) {
			Set<String> rooms = s.getAllRooms();
			if (rooms.contains(roomId ))s.sendEvent(listener, message);
		}
	}
}