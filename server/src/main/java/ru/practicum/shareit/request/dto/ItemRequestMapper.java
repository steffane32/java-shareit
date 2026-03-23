package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemDto> items) {
        if (request == null) {
            return null;
        }

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        if (items != null) {
            dto.setItems(items);
        }

        return dto;
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto createDto, User requestor) {
        if (createDto == null) {
            return null;
        }

        ItemRequest request = new ItemRequest();
        request.setDescription(createDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        return request;
    }
}