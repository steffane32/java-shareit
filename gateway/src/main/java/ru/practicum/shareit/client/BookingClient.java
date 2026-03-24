package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingState;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        return post("/bookings", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, boolean approved) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approved", approved);
        return patch("/bookings/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/bookings/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, BookingState state) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("state", state.name());
        return get("/bookings?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(Long ownerId, BookingState state) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("state", state.name());
        return get("/bookings/owner?state={state}", ownerId, parameters);
    }
}