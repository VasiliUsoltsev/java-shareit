package ru.practicum.shareit.booking;

/**
 * TODO Sprint add-bookings.
 */

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Integer booker;
    Status status;
}
