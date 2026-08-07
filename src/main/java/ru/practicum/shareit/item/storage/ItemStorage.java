package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface ItemStorage {
    public Item createItem(NewItemRequest newItemRequest, Integer userId);

    public Item updateItem(Long itemId, UpdateItemPatchRequest updateItemRequest, Integer userId);

    public void removeItem(Long itemId, Integer userId);

    public Collection<Item> getAll(Integer userId);

    public Item getItem(Long itemId);

    public Collection<Item> searchItem(String textSearch, Integer userId);
}
