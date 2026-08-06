package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    String name;
    @NotBlank(message = "Email пользователя не может быть пустым")
    @Email(message = "Email пользователя некорректный")
    String email;
}
