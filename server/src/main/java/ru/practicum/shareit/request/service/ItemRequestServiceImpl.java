package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto createDto) {
        log.info("Создание запроса пользователем id: {}", userId);

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = ItemRequestMapper.toItemRequest(createDto, requestor);

        ItemRequest savedRequest = requestRepository.save(request);
        log.info("Запрос создан с id: {}", savedRequest.getId());

        return ItemRequestMapper.toItemRequestDto(savedRequest, List.of());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Запрос списка запросов пользователя id: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<ItemRequest> requests = requestRepository.findByRequestorId(userId, SORT_BY_CREATED_DESC);

        return enrichRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        log.info("Запрос списка всех запросов пользователем id: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<ItemRequest> requests = requestRepository.findAllExceptUser(userId, SORT_BY_CREATED_DESC);

        return enrichRequestsWithItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Запрос запроса id: {} пользователем id: {}", requestId, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<Item> items = itemRepository.findAllByRequestId(requestId);

        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestDto(request, itemDtos);
    }

    private List<ItemRequestDto> enrichRequestsWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<Item> items = itemsByRequest.getOrDefault(request.getId(), List.of());
                    List<ItemDto> itemDtos = items.stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, itemDtos);
                })
                .collect(Collectors.toList());
    }
}