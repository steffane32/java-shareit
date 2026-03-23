package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestCreateDto createDto
    ) {
        log.info("POST /requests - создание запроса пользователем id: {}", userId);
        return requestService.createRequest(userId, createDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests - запрос списка запросов пользователя id: {}", userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests/all - запрос списка всех запросов пользователем id: {}", userId);
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId
    ) {
        log.info("GET /requests/{} - запрос запроса по id пользователем id: {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}