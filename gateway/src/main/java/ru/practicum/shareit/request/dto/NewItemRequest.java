package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewItemRequest {
    @NotBlank(message = "Описание необходимой вещи не может быть пустой")
    private String description;
}
