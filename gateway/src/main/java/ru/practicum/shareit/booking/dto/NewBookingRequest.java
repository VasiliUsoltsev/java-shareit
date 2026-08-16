package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NewBookingRequest {
    @NotNull(message = "Дата начала аренды не может быть пустой")
    @FutureOrPresent(message = "Дата начала аренды не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата завершения аренды не может быть пустой")
    @Future(message = "Дата завершения аренды не может быть в прошлом")
    private LocalDateTime end;

    @NotNull(message = "Нельза произвести бронирование без указания вещи")
    private Long itemId;
}
