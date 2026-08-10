package ru.practicum.shareit.item.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Feedback {
    private Long id;
    private String text;
    private LocalDateTime created;
    private Integer author;
}
