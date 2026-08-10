package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
