package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;

import java.util.Collection;

public interface ItemService {
    public Collection<ItemDto> getAll(Integer userId);

    public ItemDto getItem(Long itemId);

    public Collection<ItemDto> searchItem(String text, Integer userId);

    public ItemDto createItem(NewItemRequest newItem, Integer userId);

    public ItemDto updateItem(Long itemId, UpdateItemPatchRequest updateItem, Integer userId);

    public void removeItem(Long itemId, Integer userId);
}
