package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UpdateUserPatchRequest {
    private String name;
    private String email;
}
