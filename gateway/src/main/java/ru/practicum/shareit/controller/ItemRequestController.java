package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestCreateDto;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemRequestCreateDto requestDto
    ) {
        log.info("POST /requests - создание запроса пользователем id: {}", userId);
        return itemRequestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests - запрос списка запросов пользователя id: {}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests/all - запрос списка всех запросов пользователем id: {}", userId);
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        log.info("GET /requests/{} - запрос запроса по id пользователем id: {}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}