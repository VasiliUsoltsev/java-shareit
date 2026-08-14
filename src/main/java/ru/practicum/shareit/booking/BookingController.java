package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingResponse> getBookingAllForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingAllForBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponse> getBookingAllForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingAllForOwner(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody NewBookingRequest newBookingRequest,
                                         @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.createBookingRequest(newBookingRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse resolveBooking(@PathVariable Long bookingId,
                                          @RequestParam boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.resolveBooking(bookingId, approved, userId);
    }
}