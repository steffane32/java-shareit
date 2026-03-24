package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestCreateDto requestDto) {
        return post("/requests", userId, requestDto);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get("/requests/all", userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/requests/" + requestId, userId);
    }
}