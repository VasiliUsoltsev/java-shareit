package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForItemRequestResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestBaseResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {
    public static ItemRequestBaseResponse mapToItemRequestBaseResponse(ItemRequest itemRequest) {
        ItemRequestBaseResponse itemRequestBaseResponse = new ItemRequestBaseResponse();

        itemRequestBaseResponse.setId(itemRequest.getId());
        itemRequestBaseResponse.setDescription(itemRequest.getDescription());
        itemRequestBaseResponse.setCreated(itemRequest.getCreated());

        return itemRequestBaseResponse;
    }

    public static ItemRequest mapToItemRequest(NewItemRequest newItemRequest) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(newItemRequest.getDescription());

        return itemRequest;
    }

    public static ItemRequestWithAnswersResponse mapToItemRequestWithAnswersResponse(ItemRequest itemRequest) {
        ItemRequestWithAnswersResponse itemRequestWithAnswersResponse = new ItemRequestWithAnswersResponse();

        itemRequestWithAnswersResponse.setId(itemRequest.getId());
        itemRequestWithAnswersResponse.setDescription(itemRequest.getDescription());
        itemRequestWithAnswersResponse.setCreated(itemRequest.getCreated());

        List<ItemForItemRequestResponse> items = itemRequest.getItems()
                .stream()
                .map(ItemMapper::mapToItemForItemRequestResponse)
                .toList();

        itemRequestWithAnswersResponse.setItems(items);

        return itemRequestWithAnswersResponse;
    }
}
