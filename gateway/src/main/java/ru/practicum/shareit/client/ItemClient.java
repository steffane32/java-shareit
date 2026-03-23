package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.CommentCreateDto;
import ru.practicum.shareit.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("/items", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/items/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsByOwner(Long userId) {
        return get("/items", userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        return get("/items/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        return post("/items/" + itemId + "/comment", userId, commentCreateDto);
    }
}