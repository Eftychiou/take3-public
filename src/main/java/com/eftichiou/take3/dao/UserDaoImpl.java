package com.eftichiou.take3.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
//import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.eftichiou.take3.dto.ItemWithUser;
import com.eftichiou.take3.dto.UserWithAuthentication;
import com.eftichiou.take3.dto.UserWithoutCredentials;
import com.eftichiou.take3.dto.requests.RoomIdAndPass;
import com.eftichiou.take3.entity.Item;
import com.eftichiou.take3.entity.Room;
import com.eftichiou.take3.entity.User;
import com.eftichiou.take3.exceptions.CustomException;
import com.eftichiou.take3.rest.Controllers;

@Repository
public class UserDaoImpl implements UserDao, UserDetailsService {
    private final EntityManager entityManager;
    private static final Logger log = LoggerFactory.getLogger(Controllers.class);

    @Autowired
    public UserDaoImpl(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.findByEmail(username);
        if (user == null) throw new UsernameNotFoundException("User not found in the database");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        SimpleGrantedAuthority au = new SimpleGrantedAuthority(String.valueOf(user.getId()));
        authorities.add(au);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public List<User> findAll() {
        @SuppressWarnings("unchecked")
        List<User> userList = entityManager.createQuery("from User").getResultList();
        return userList;
    }

    @Override
    public UserWithoutCredentials findById(int id) {
        User user = entityManager.find(User.class, id); //doesn't need @Transactional because no session is needed
        // just a query
        return new UserWithoutCredentials(user.getId(), user.getFirstName(), user.getLastName()
                , user.getAvatarImgUrl());
    }

    @Override
    //	@Transactional
    public void deleteById(int id) {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.beginTransaction();
            User user = session.get(User.class, id);
            session.delete(user);
            session.getTransaction().commit();
            session.close();
        } catch (Exception exc) {
            throw new CustomException(exc);
        }
    }

    @Override
    public User findByEmail(String email) {
        Query findUser = entityManager.createQuery("from User u where u.email='" + email + "'");
        return (User) findUser.getSingleResult();
    }

    @Override
    @Transactional
    public User addUser(User user) {
        try {
            User theUser = entityManager.merge(user);
            theUser.setAvatarImgUrl("public/user-photos/" + theUser.getId() + "/" + user.getAvatarImgUrl());
            theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
            return theUser;
        } catch (Exception exc) {
            log.debug(exc.getMessage());
            throw new CustomException("User Exists or Missing Information", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public User loginUser(User user) {
        try {
            Query userByEmail = entityManager.createQuery(
                    "from User u where u.email='" + user.getEmail() + "' and u.password='" + user.getPassword() + "'");
            return (User) userByEmail.getSingleResult();
        } catch (Exception exc) {
            log.debug(exc.getMessage());
            throw new RuntimeException("Wrong Email or Password");
        }
    }

    @Override
    @Transactional
    public void joinRoom(RoomIdAndPass rpwe, User theUser) throws Exception {
        try {
            User user = entityManager.find(User.class, theUser.getId());
            Room room = entityManager.find(Room.class, rpwe.getRoomId());
            if (!room.getPassword().equals(rpwe.getPassword())) throw new Exception("Wrong Password");
            List<Room> allRooms = user.getRooms();
            if (allRooms == null) allRooms = new ArrayList<>();
            if (!allRooms.contains(room)) allRooms.add(room);
//			RoomWithUsersAndUserId riui = new RoomWithUsersAndUserId(room.getId(), user.getId());			
            List<UserWithoutCredentials> usersWC = new ArrayList<>();
            List<User> users = room.getUsers();
            for (User u : users) {//
                UserWithoutCredentials uwc = new UserWithoutCredentials(u.getId(), u.getFirstName(), u.getLastName(),
                        u.getAvatarImgUrl());
                usersWC.add(uwc);//
            }
            UserWithoutCredentials currentUser = new UserWithoutCredentials(user.getId(), user.getFirstName(),
                    user.getLastName(), user.getAvatarImgUrl());
            usersWC.add(currentUser);
//			RoomWithoutPassword rwp = new RoomWithoutPassword(room.getId(),room.getRoomName(),usersWC);			
//			riui.setRwp(rwp);
            List<Item> items = room.getItems();
            List<ItemWithUser> itemsWithUser = new ArrayList<>();
            for (Item item : items) {
                ItemWithUser itemWithUser = new ItemWithUser(item.getId(), item.getItemName());
                itemWithUser.setCarrier(new UserWithoutCredentials());
                if (item.getUser() != null) {
                    itemWithUser.setCarrier(new UserWithoutCredentials(item.getUser().getId(),
                            item.getUser().getFirstName(), item.getUser().getLastName(),
                            item.getUser().getAvatarImgUrl()));
                }
                itemsWithUser.add(itemWithUser);
            }
//			riui.setItems(itemsWithUser);			
//			return riui;
        } catch (Exception exc) {
            log.debug(exc.getMessage());
            throw exc;
        }
    }

    @Override
    @Transactional
    public void leaveRoom(int roomId, int userId) {
        User user = entityManager.find(User.class, userId);
        Room room = entityManager.find(Room.class, roomId);
        List<Room> allRooms = user.getRooms();
        allRooms.remove(room);
    }

    @Override
    @Transactional
    public void leaveRoom(int roomId, User theUser) {
        User user = entityManager.find(User.class, theUser.getId());
        Room room = entityManager.find(Room.class, roomId);
        //if you want to clean items carrier every time user leave the room enable the code bellow
//        List<Item> items = room.getItems();
//        for (Item i : items) {
//            if (i.getUser() == user) i.setUser(null);
//        }
        List<Room> allRooms = user.getRooms();
        allRooms.remove(room);
    }

    @Override
    @Transactional
    public UserWithAuthentication findUserWithAuthStatus(SocketIOClient client) {
        try {
            HandshakeData handshakeData = client.getHandshakeData();
            String access_token = handshakeData.getSingleUrlParam("access_token");
            if (access_token != null && access_token.startsWith("Bearer ")) {
                String token = access_token.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String email = decodedJWT.getSubject();
                User user = this.findByEmail(email);
                return new UserWithAuthentication(true, user);
            } else {
                throw new Exception("Unauthenticated");
            }
        } catch (Exception exc) {
            client.sendEvent("error", "AUTHENTICATION_FAILED", exc.getMessage());
            client.disconnect();
            return new UserWithAuthentication(false, null);
//			throw new Exception(exc.getMessage());
        }
    }
}
