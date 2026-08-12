package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    public Item createItem(NewItemRequest newItemRequest, Long userId);

    public Item updateItem(Long itemId, UpdateItemPatchRequest updateItemRequest, Long userId);

    public void removeItem(Long itemId, Long userId);

    public Collection<Item> getAll(Long userId);

    public Item getItem(Long itemId);

    public Collection<Item> searchItem(String textSearch, Long userId);
}
