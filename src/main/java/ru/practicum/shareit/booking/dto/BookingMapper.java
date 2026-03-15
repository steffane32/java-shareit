package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        BookingDto.ItemDto itemDto = new BookingDto.ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        dto.setItem(itemDto);

        BookingDto.BookerDto bookerDto = new BookingDto.BookerDto();
        bookerDto.setId(booking.getBooker().getId());
        bookerDto.setName(booking.getBooker().getName());
        dto.setBooker(bookerDto);

        dto.setItemId(booking.getItem().getId());

        return dto;
    }
}