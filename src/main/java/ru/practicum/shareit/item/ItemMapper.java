package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemResponse mapToItemResponse(Item item) {
        ItemResponse itemResponse = new ItemResponse();

        itemResponse.setId(item.getId());
        itemResponse.setName(item.getName());
        itemResponse.setDescription(item.getDescription());
        itemResponse.setAvailable(item.getAvailable());

        return itemResponse;
    }

    public static Item mapToItem(NewItemRequest newItemRequest) {
        Item item = new Item();

        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setAvailable(newItemRequest.getAvailable());

        return item;
    }

    public static Item mapToItem(UpdateItemPatchRequest updateItemRequest) {
        Item item = new Item();

        item.setName(updateItemRequest.getName());
        item.setDescription(updateItemRequest.getDescription());
        item.setAvailable(updateItemRequest.getAvailable());

        return item;
    }
}
