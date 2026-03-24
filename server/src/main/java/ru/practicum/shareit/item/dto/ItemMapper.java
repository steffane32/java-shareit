package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }

        return dto;
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        return item;
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item, List<Booking> bookings, List<Comment> comments) {
        if (item == null) {
            return null;
        }

        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max((b1, b2) -> b1.getEnd().compareTo(b2.getEnd()))
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                .orElse(null);

        if (lastBooking != null) {
            ItemWithBookingsDto.BookingInfoDto lastBookingDto = new ItemWithBookingsDto.BookingInfoDto();
            lastBookingDto.setId(lastBooking.getId());
            lastBookingDto.setBookerId(lastBooking.getBooker().getId());
            dto.setLastBooking(lastBookingDto);
        }

        if (nextBooking != null) {
            ItemWithBookingsDto.BookingInfoDto nextBookingDto = new ItemWithBookingsDto.BookingInfoDto();
            nextBookingDto.setId(nextBooking.getId());
            nextBookingDto.setBookerId(nextBooking.getBooker().getId());
            dto.setNextBooking(nextBookingDto);
        }

        if (comments != null) {
            List<CommentDto> commentDtos = comments.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
            dto.setComments(commentDtos);
        } else {
            dto.setComments(List.of());
        }

        return dto;
    }
}