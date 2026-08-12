package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingItemResponse;

import java.util.Set;

@Setter
@Getter
public class ItemBookingDateResponse {
    private Long id;
    private String name;
    private BookingItemResponse lastBooking;
    private BookingItemResponse nextBooking;
    private String description;
    private Boolean available;
    private Set<CommentResponse> comments;
}
