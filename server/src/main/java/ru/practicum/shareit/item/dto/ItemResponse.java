package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Set<CommentResponse> comments;
}
