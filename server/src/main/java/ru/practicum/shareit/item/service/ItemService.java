package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> getAllItemsByOwner(Long ownerId);

    ItemWithBookingsDto getItemById(Long id, Long userId);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}