package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class ItemBookingDateResponse {
    private Long id;
    private String name;
    private LocalDateTime startLast;
    private LocalDateTime endLast;
    private LocalDateTime startNext;
    private LocalDateTime endNext;
    private String description;
    private Boolean available;
    private Set<CommentResponse> comments;
}
