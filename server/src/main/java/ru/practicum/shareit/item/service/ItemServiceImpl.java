package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public List<ItemWithBookingsDto> getAllItemsByOwner(Long ownerId) {
        log.info("Запрос всех вещей владельца с id: {}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        // ИСПРАВЛЕНО: используем новый метод с параметром status
        Map<Long, List<Booking>> bookingsByItem = bookingRepository
                .findBookingsByItemIdsAndStatus(itemIds, Booking.BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<Comment>> commentsByItem = commentRepository
                .findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), List.of());
                    List<Comment> itemComments = commentsByItem.getOrDefault(item.getId(), List.of());
                    return ItemMapper.toItemWithBookingsDto(item, itemBookings, itemComments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemWithBookingsDto getItemById(Long id, Long userId) {
        log.info("Запрос вещи с id: {}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        List<Booking> bookings = List.of();
        if (item.getOwner().getId().equals(userId)) {
            bookings = bookingRepository.findBookingsByItemIdAndStatus(id, Booking.BookingStatus.APPROVED);
        }

        List<Comment> comments = commentRepository.findByItemId(id);

        return ItemMapper.toItemWithBookingsDto(item, bookings, comments);
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Создание новой вещи для пользователя с id: {}", userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id " + itemDto.getRequestId() + " не найден"));
            item.setRequest(request);
        }

        Item createdItem = itemRepository.save(item);
        log.info("Вещь создана с id: {}", createdItem.getId());

        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Обновление вещи с id: {} от пользователя с id: {}", itemId, userId);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("Пользователь с id {} не является владельцем вещи с id {}", userId, itemId);
            throw new NotFoundException("Пользователь с id " + userId + " не является владельцем вещи с id " + itemId);
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Вещь с id: {} обновлена", itemId);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: {}", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        log.info("Добавление комментария к вещи id: {} от пользователя id: {}", itemId, userId);

        if (commentCreateDto.getText() == null || commentCreateDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        LocalDateTime now = LocalDateTime.now();

        boolean hasUserBooked = bookingRepository.hasUserBookedAndFinished(
                itemId,
                userId,
                Booking.BookingStatus.APPROVED,
                now
        );

        if (!hasUserBooked) {
            throw new ValidationException("Пользователь не может оставить отзыв, так как не арендовал эту вещь");
        }

        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий добавлен с id: {}", savedComment.getId());

        return CommentMapper.toCommentDto(savedComment);
    }
}