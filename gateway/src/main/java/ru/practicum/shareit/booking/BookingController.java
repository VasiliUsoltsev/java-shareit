package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.StatusRequest;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingAllForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state
    ) {
        StatusRequest stateParam = StatusRequest.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Неопознанный state"));

        return bookingClient.getBookingAllForBooker(userId, stateParam);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingAllForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state
    ) {
        StatusRequest stateParam = StatusRequest.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Неопознанный state"));

        return bookingClient.getBookingAllForOwner(userId, stateParam);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody NewBookingRequest newBookingRequest,
                                                @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        LocalDateTime start = newBookingRequest.getStart();
        LocalDateTime end = newBookingRequest.getEnd();

        if (start.equals(end)) {
            throw new ValidationException("Дата начала бронирования и завершения не должны совпадать");
        }

        return bookingClient.createBookingRequest(newBookingRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> resolveBooking(@PathVariable Long bookingId,
                                                 @RequestParam boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingClient.resolveBooking(bookingId, approved, userId);
    }
}