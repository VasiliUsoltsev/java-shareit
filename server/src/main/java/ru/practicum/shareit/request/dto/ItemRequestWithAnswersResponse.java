package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemForItemRequestResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestWithAnswersResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForItemRequestResponse> items;
}
