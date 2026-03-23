package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingState;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody BookingCreateDto bookingCreateDto
    ) {
        log.info("POST /bookings - создание бронирования пользователем id: {}", userId);
        return bookingClient.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("PATCH /bookings/{}?approved={} - подтверждение/отклонение бронирования", bookingId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("GET /bookings/{} - получение данных о бронировании", bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {
        log.info("GET /bookings?state={} - список бронирований пользователя id: {}", state, userId);
        return bookingClient.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {
        log.info("GET /bookings/owner?state={} - список бронирований для вещей владельца id: {}", state, ownerId);
        return bookingClient.getOwnerBookings(ownerId, state);
    }
}