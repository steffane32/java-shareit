package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("/users", userDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        return patch("/users/" + userId, userId, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/users/" + userId, userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("/users");
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/users/" + userId, userId);
    }
}