package ru.practicum.shareit.item.dto;

import lombok.Getter;

@Getter
public class UpdateItemPatchRequest {
    private String name;
    private String description;
    private Boolean available;
}
