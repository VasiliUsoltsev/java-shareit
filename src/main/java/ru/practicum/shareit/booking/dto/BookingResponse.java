package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookingResponse;
import ru.practicum.shareit.user.dto.UserBookingResponse;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemBookingResponse item;
    private UserBookingResponse booker;
    private Status status;
}