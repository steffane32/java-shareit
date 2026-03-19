package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.util.List;

@Data
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentDto> comments;

    @Data
    public static class BookingInfoDto {
        private Long id;
        private Long bookerId;
    }
}