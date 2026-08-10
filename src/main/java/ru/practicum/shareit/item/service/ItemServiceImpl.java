package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public Collection<ItemResponse> getAll(Integer userId) {
        return itemStorage.getAll(userId)
                .stream()
                .map(ItemMapper::mapToItemResponse)
                .toList();
    }

    @Override
    public ItemResponse getItem(Long itemId) {
        Item item = itemStorage.getItem(itemId);

        return ItemMapper.mapToItemResponse(item);
    }

    @Override
    public Collection<ItemResponse> searchItem(String text, Integer userId) {
        return itemStorage.searchItem(text, userId)
                .stream()
                .map(ItemMapper::mapToItemResponse)
                .toList();
    }

    @Override
    public ItemResponse createItem(NewItemRequest newItem, Integer userId) {
        Item item = itemStorage.createItem(newItem, userId);

        return ItemMapper.mapToItemResponse(item);
    }

    @Override
    public ItemResponse updateItem(Long itemId, UpdateItemPatchRequest updateItem, Integer userId) {
        Item item = itemStorage.updateItem(itemId, updateItem, userId);

        return ItemMapper.mapToItemResponse(item);
    }

    @Override
    public void removeItem(Long itemId, Integer userId) {
        itemStorage.removeItem(itemId, userId);
    }
}
