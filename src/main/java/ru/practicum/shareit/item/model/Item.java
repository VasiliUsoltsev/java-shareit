package ru.practicum.shareit.item.model;

import lombok.Data;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer request;
    private List<Feedback> feedbacks;
}
