package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserPatchRequest {
    private String name;
    @Email(message = "Email пользователя некорректный")
    private String email;
}
