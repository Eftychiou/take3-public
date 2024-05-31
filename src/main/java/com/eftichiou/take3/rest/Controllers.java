package com.eftichiou.take3.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.eftichiou.take3.dao.ItemDao;
import com.eftichiou.take3.dao.RoomDao;
import com.eftichiou.take3.dao.UserDao;
import com.eftichiou.take3.dto.RoomWithAdminId;
import com.eftichiou.take3.dto.RoomWithoutPassword;
import com.eftichiou.take3.dto.UserWithoutCredentials;
import com.eftichiou.take3.entity.Item;
import com.eftichiou.take3.entity.User;
import com.eftichiou.take3.tools.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class Controllers {
    @Autowired
    private UserDao userDAO;
    @Autowired
    private RoomDao roomDAO;
    @Autowired
    private ItemDao itemDAO;

    @GetMapping("/")
    public String sayHello() {
        return "You shouldn't be here";
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@ModelAttribute User theUser,
                                         @RequestParam("avatar") MultipartFile multipartFile) {
        try {
            if (multipartFile == null || multipartFile.isEmpty() || multipartFile.getSize() > 1000000) {
                throw new Exception("Avatar size must be lower than 1MB");
            }
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            theUser.setAvatarImgUrl(fileName);
            User user = userDAO.addUser(theUser);
            String uploadDir = "./public/user-photos/" + user.getId() + "/";
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "User signed up successfully");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception exc) {
            exc.printStackTrace();
            HashMap<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Error during user signup");
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get_room_list")
    public List<RoomWithoutPassword> getRoomList() {
        return roomDAO.findAllRoomsWithUsers();
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<Object> deleteUser(@RequestParam("userId") int userId) {
        userDAO.deleteById(userId);
        HashMap<String, String> json = new HashMap<>();
        json.put("message", "deleted");
        return new ResponseEntity<Object>(json, HttpStatus.OK);
    }

    @GetMapping("/find_user/{userId}")
    public UserWithoutCredentials findUser(@PathVariable int userId) {
        return userDAO.findById(userId);
    }

    @GetMapping("/get_room_admin/{roomId}")
    public UserWithoutCredentials findRoomAdmin(@PathVariable int roomId) {
        return roomDAO.findRoomAdmin(roomId);
    }

    @GetMapping("find_all_users")
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @PostMapping("/find_users_of_room/{roomId}")
    public List<UserWithoutCredentials> getUsersOfRoom(@PathVariable int roomId) {
        return roomDAO.findUsers(roomId);
    }

    @DeleteMapping("/delete_room")
    public ResponseEntity<Object> deleteRoom(@RequestParam("roomId") int roomId) {
        roomDAO.deleteRoom(roomId);
        HashMap<String, String> json = new HashMap<>();
        json.put("message", "deleted");
        return new ResponseEntity<Object>(json, HttpStatus.OK);
    }

    @PostMapping("/add_item/{itemName}/{roomId}")
    public void addItem(@PathVariable String itemName, @PathVariable int roomId) {
        itemDAO.addItem(itemName, roomId);
    }

    @GetMapping("/get_all_items_of_room/{roomId}")
    public List<Item> getAllItems(@PathVariable int roomId) {
        return itemDAO.findAllItemsOfRoom(roomId);
    }

    @GetMapping("/get_carrier_of_item/{itemId}")
    public UserWithoutCredentials userUserOfItem(@PathVariable int itemId) {
        return itemDAO.getUser(itemId);
    }

    @GetMapping("/refresh/token/{token}")
    public void refreshToken(@PathVariable String token, HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String refresh_token = token.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                // This will give the username that comes with the token
                String email = decodedJWT.getSubject();
                User user = userDAO.findByEmail(email);
                String access_token = JWT.create()
                        .withSubject(user.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", new ArrayList<GrantedAuthority>())
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exc) {
                response.setHeader("error", exc.getMessage());
                response.setStatus(403);
                Map<String, String> error = new HashMap<>();
                error.put("message", exc.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
                // add Case if not authencticated to remove the sessionStorage for refresh token
            }
        } else {
            throw new RuntimeException("Refresh Token is Missing or Expired");
        }
    }
}
