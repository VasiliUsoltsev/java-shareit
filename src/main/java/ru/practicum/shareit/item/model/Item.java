package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Integer ownerId;
    Integer request;
    List<Feedback> feedbacks;
}
