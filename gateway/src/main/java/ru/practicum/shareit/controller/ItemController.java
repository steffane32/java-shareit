package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.CommentCreateDto;
import ru.practicum.shareit.dto.ItemDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /items - запрос всех вещей владельца с id: {}", userId);
        return itemClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId
    ) {
        log.info("GET /items/{} - запрос вещи по id пользователем id: {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("POST /items - создание новой вещи для пользователя с id: {}, данные: {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH /items/{} - обновление вещи пользователем с id: {}, данные: {}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("GET /items/search?text={} - поиск вещей", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentCreateDto
    ) {
        log.info("POST /items/{}/comment - добавление комментария пользователем id: {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentCreateDto);
    }
}