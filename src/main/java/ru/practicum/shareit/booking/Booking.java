package ru.practicum.shareit.booking;

/**
 * TODO Sprint add-bookings.
 */

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Integer booker;
    private Status status;
}
