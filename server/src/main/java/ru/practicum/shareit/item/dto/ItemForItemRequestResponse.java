package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemForItemRequestResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
}
