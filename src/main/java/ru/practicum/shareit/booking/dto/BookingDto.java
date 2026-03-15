package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private BookerDto booker;
    private ItemDto item;
    private Booking.BookingStatus status;

    @Data
    public static class BookerDto {
        private Long id;
        private String name;
    }

    @Data
    public static class ItemDto {
        private Long id;
        private String name;
    }
}