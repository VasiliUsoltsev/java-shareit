package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;

import java.util.Collection;

public interface ItemService {
    public Collection<ItemResponse> getAll(Integer userId);

    public ItemResponse getItem(Long itemId);

    public Collection<ItemResponse> searchItem(String text, Integer userId);

    public ItemResponse createItem(NewItemRequest newItem, Integer userId);

    public ItemResponse updateItem(Long itemId, UpdateItemPatchRequest updateItem, Integer userId);

    public void removeItem(Long itemId, Integer userId);
}
