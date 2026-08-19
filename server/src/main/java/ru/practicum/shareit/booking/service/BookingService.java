package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.Collection;

public interface BookingService {
    public BookingResponse createBookingRequest(NewBookingRequest newBookingRequest, Long userId);

    public BookingResponse resolveBooking(Long bookingId, boolean approve, Long userId);

    public BookingResponse getBookingById(Long bookingId, Long userId);

    public Collection<BookingResponse> getBookingAllForBooker(Long userId, String state);

    public Collection<BookingResponse> getBookingAllForOwner(Long userId, String state);
}