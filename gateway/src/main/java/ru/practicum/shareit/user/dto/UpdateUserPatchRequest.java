package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserPatchRequest {
    private String name;

    @NotBlank(message = "Email  не может быть пустым")
    @Email(message = "Email пользователя некорректный")
    private String email;
}
