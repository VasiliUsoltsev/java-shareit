package ru.practicum.shareit.item.dto;

import lombok.Getter;

@Getter
public class UpdateItemPatchRequest {
    String name;
    String description;
    Boolean available;
}
