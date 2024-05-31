package com.eftichiou.take3.socketListeners;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.eftichiou.take3.dao.ItemDao;
import com.eftichiou.take3.dao.RoomDao;
import com.eftichiou.take3.dao.UserDao;
import com.eftichiou.take3.dto.Message;
import com.eftichiou.take3.dto.UserWithAuthentication;
import com.eftichiou.take3.dto.requests.RoomIdAndItemId;
import com.eftichiou.take3.dto.requests.RoomIdAndItemName;
import com.eftichiou.take3.dto.requests.RoomIdAndPass;
import com.eftichiou.take3.dto.responses.RoomWithUsersWithItems;
import com.eftichiou.take3.dto.responses.RoomWithoutPassWithoutUsers;
import com.eftichiou.take3.entity.Room;
import com.eftichiou.take3.entity.User;
import com.eftichiou.take3.tools.Tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoomListeners {
    private final SocketIONamespace namespace;
    @Autowired
    private RoomDao roomDAO;
    @Autowired
    private UserDao userDAO;
    @Autowired
    private ItemDao itemDAO;

    @Autowired
    public RoomListeners(SocketIOServer server) {
        this.namespace = server.addNamespace("/rooms");
        this.namespace.addConnectListener(onConnected());
        this.namespace.addEventListener("roomCreate", Room.class, onRoomCreate());
        this.namespace.addEventListener("joinRoom", RoomIdAndPass.class, onJoinRoom());
        this.namespace.addEventListener("onLeaveRoom", String.class, onLeaveRoom());
        this.namespace.addEventListener("onDeleteRoom", String.class, onDeleteRoom());
        this.namespace.addEventListener("onAddItem", RoomIdAndItemName.class, onAddItem());
        this.namespace.addEventListener("onDeleteItem", RoomIdAndItemId.class, onDeleteItem());
        this.namespace.addEventListener("onToggleUser", RoomIdAndItemId.class, onToggleUser());
        this.namespace.addEventListener("onNewMessage", Message.class, onMessage());
        this.namespace.addDisconnectListener(onDisconnected());
    }

    private DataListener<Message> onMessage() {
        return (client, data, ackSender) -> {
            UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
            if (!user.getIsAuthenticated())
                throw new Exception("Authentication Failed");
            data.setDate();
            data.setUserId(user.getUser().getId());
            data.setFirstName(user.getUser().getFirstName());
            data.setLastName(user.getUser().getLastName());
            Tools.broadcastMessage(namespace, String.valueOf(data.getRoomId()), "onMessages", data);
        };
    }

    private DataListener<Room> onRoomCreate() {
        return (client, data, ackSender) -> {
            try {
                UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
                if (!user.getIsAuthenticated())
                    throw new Exception("Authentication Failed");
                List<RoomWithoutPassWithoutUsers> allRooms = roomDAO.addRoom(data.getRoomName(),
                        user.getUser().getEmail(),
                        data.getPassword());
                namespace.getBroadcastOperations().sendEvent("roomListChanges", allRooms);
                client.sendEvent("success", "ROOM_CREATED");
            } catch (Exception exc) {
                client.sendEvent("error", "ROOM_CREATE_FAIL", exc.getMessage());
            }
        };
    }

    private DataListener<RoomIdAndPass> onJoinRoom() {
        return (client, data, ackSender) -> {
            try {
                UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
                if (!user.getIsAuthenticated())
                    throw new Exception("Validation Failed");
                User theUser = user.getUser();
                List<Room> theRooms = theUser.getRooms();
                Collection<SocketIOClient> clients = namespace.getAllClients();
                for (Room r : theRooms) {
                    for (SocketIOClient s : clients) {
                        Set<String> rooms = s.getAllRooms();
                        userDAO.leaveRoom(r.getId(), theUser);
                        RoomWithUsersWithItems room = roomDAO.calculateRoomChanges(r.getId());
                        if (rooms.contains(String.valueOf(r.getId())))
                            s.sendEvent("onRoomUpdates", room);
                    }
                    client.leaveRoom(String.valueOf(r.getId()));
                }
                // join the new room
                String roomIdString = Integer.toString(data.getRoomId());
                userDAO.joinRoom(data, user.getUser());
                client.joinRoom(roomIdString);
                RoomWithUsersWithItems changes = roomDAO.calculateRoomChanges(data.getRoomId());
                Tools.broadcastChanges(namespace, String.valueOf(data.getRoomId()), "onRoomUpdates", changes);
                client.sendEvent("success", "ROOM_JOINED");
            } catch (Exception exc) {
                if (exc.getMessage().equals("DUPLICATE"))
                    client.disconnect();
                else
                    client.sendEvent("error", "ROOM_JOIN_FAIL", exc.getMessage());
            }
        };
    }

    private DataListener<String> onLeaveRoom() {
        return (client, data, ackSender) -> {
            try {
                int roomId = Integer.parseInt(data);
                UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
                if (!user.getIsAuthenticated()) throw new Exception("Validation Failed");
                userDAO.leaveRoom(Integer.parseInt(data), user.getUser());
                client.leaveRoom(data);
                RoomWithUsersWithItems changes = roomDAO.calculateRoomChanges(roomId);
                Tools.broadcastChanges(namespace, data, "onRoomUpdates", changes);
                client.sendEvent("success", "ROOM_LEFT");
            } catch (Exception exc) {
                exc.printStackTrace();
                client.sendEvent("error", "ROOM_LEFT", exc.getMessage());
            }
        };
    }

    private DataListener<String> onDeleteRoom() {
        return (client, data, ackSender) -> {
            try {
                UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
                if (!user.getIsAuthenticated())
                    throw new Exception("Authentication Failed");
                int userId = user.getUser().getId();
                int roomAdmin = roomDAO.findRoomAdmin(Integer.parseInt(data)).getId();
                if (userId != roomAdmin) throw new Exception("You are not authorize to delete this room");
                List<RoomWithoutPassWithoutUsers> theRooms = roomDAO.deleteRoom(Integer.parseInt(data));
                Tools.broadcastChanges(namespace, data, "onRoomUpdates", theRooms);
            } catch (Exception exc) {
                client.sendEvent("error", "ROOM_CREATE_FAIL", exc.getMessage());
            }
        };
    }

    private DataListener<RoomIdAndItemId> onToggleUser() {
        return (client, data, ackSender) -> {
            try {
                UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
                if (!user.getIsAuthenticated())
                    throw new Exception("Validation Failed");
                itemDAO.toggleCarrier(data, user.getUser().getId());
                RoomWithUsersWithItems changes = roomDAO.calculateRoomChanges(data.getRoomId());
                Tools.broadcastChanges(namespace, String.valueOf(data.getRoomId()), "onRoomUpdates", changes);
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
        };
    }

    private DataListener<RoomIdAndItemId> onDeleteItem() {
        return (client, data, ackSender) -> {
            UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
            if (!user.getIsAuthenticated())
                throw new Exception("Validation Failed");
            itemDAO.deleteItem(data);
            RoomWithUsersWithItems changes = roomDAO.calculateRoomChanges(data.getRoomId());
            Tools.broadcastChanges(namespace, String.valueOf(data.getRoomId()), "onRoomUpdates", changes);
        };
    }

    private DataListener<RoomIdAndItemName> onAddItem() {
        return (client, data, ackSender) -> {
            UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
            if (!user.getIsAuthenticated())
                throw new Exception("Validation Failed");
            itemDAO.addItem(data.getItemName(), data.getRoomId());
            RoomWithUsersWithItems changes = roomDAO.calculateRoomChanges(data.getRoomId());
            Tools.broadcastChanges(namespace, String.valueOf(data.getRoomId()), "onRoomUpdates", changes);
            client.sendEvent("success", "ITEM_ADDED");
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            HandshakeData handshakeData = client.getHandshakeData();
            String acc_tok = handshakeData.getSingleUrlParam("access_token");
            try {
                boolean canContinue = true;
                String currentUserEmail = userDAO.findUserWithAuthStatus(client).getUser().getEmail();
                List<SocketIOClient> clientsList = new ArrayList<>();
                for (SocketIOClient c : this.namespace.getAllClients()) {
                    String loopedUser = userDAO.findUserWithAuthStatus(c).getUser().getEmail();
                    if (currentUserEmail.equals(loopedUser)) {
                        clientsList.add(c);
                        if (clientsList.size() > 1) {
                            for (SocketIOClient s : clientsList) {
                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        s.disconnect();
                                    }
                                }, 10000);
                                s.sendEvent("error", "DUPLICATION",
                                        "This account was attempted to be used from 2 terminals at the same time");
                            }
                            canContinue = false;
                        }
                    }
                }
                if (!canContinue) {
                    throw new Exception();
                }
                Boolean isValidated = Tools.valideToken(acc_tok);
                if (!isValidated) {
                    throw new Exception("Unauthenticated");
                }
                List<RoomWithoutPassWithoutUsers> rwp = roomDAO.findAllRoomsWithoutUsers();
                namespace.getBroadcastOperations().sendEvent("connect", rwp);
                System.out.println(client.getSessionId().toString() + " -> " + client.getRemoteAddress() + " " +
                        "connected");
            } catch (Exception exc) {
                // client.disconnect();
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            try {
                UserWithAuthentication user = userDAO.findUserWithAuthStatus(client);
                User theUser = user.getUser();
                List<Room> theRooms = theUser.getRooms();
                for (Room r : theRooms) {
                    RoomWithUsersWithItems changes = roomDAO.calculateRoomChanges(r.getId());
                    userDAO.leaveRoom(r.getId(), theUser);
                    client.leaveRoom(String.valueOf(r.getId()));
                    Tools.broadcastChanges(namespace, String.valueOf(r.getId()), "onRoomUpdates", changes);
                }
                System.out.println(client.getSessionId().toString() + " -> " + client.getRemoteAddress() + " " +
                        "disconnected");
            } catch (Exception exc) {
//				exc.printStackTrace();
            }
        };
    }
}
