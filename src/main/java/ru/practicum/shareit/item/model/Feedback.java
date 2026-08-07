package ru.practicum.shareit.item.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Feedback {
    Long id;
    String text;
    LocalDateTime created;
    Integer author;
}
