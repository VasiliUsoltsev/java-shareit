package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items;
    private final UserStorage userStorage;

    private static final String USER_NOT_FOUND_EXCEPTION = "Пользователь с данным идентификатором не найден";
    private static final String ITEM_NOT_FOUND_EXCEPTION = "Вещь с данным идентификатором не найдена";
    private static final String ITEM_ACCESS_DENIED_EXCEPTION = "У пользователя нет прав редактировать данную вещь";

    @Override
    public Item createItem(NewItemRequest newItemRequest, Integer userId) {
        log.debug("Мы получили на вход - " + newItemRequest);
        Item item = ItemMapper.mapToItem(newItemRequest);

        Long itemId = getNextId();
        item.setId(itemId);

        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND_EXCEPTION);
        }
        item.setOwnerId(userId);

        items.put(itemId, item);

        log.debug("Мы создаем такой обьект - " + item);

        return item;
    }

    @Override
    public Item updateItem(Long itemId, UpdateItemPatchRequest updateItemRequest, Integer userId) {
        Item updateItem = ItemMapper.mapToItem(updateItemRequest);

        updateItem.setId(itemId);

        Item oldItem = items.get(itemId);

        if (oldItem != null) {
            if (!userStorage.existsById(userId)) {
                throw new NotFoundException(USER_NOT_FOUND_EXCEPTION);
            }

            if (!oldItem.getOwnerId().equals(userId)) {
                throw new AccessDeniedException(ITEM_ACCESS_DENIED_EXCEPTION);
            }

            if (updateItem.getName() != null) {
                oldItem.setName(updateItem.getName());
            }

            if (updateItem.getDescription() != null) {
                oldItem.setDescription(updateItem.getDescription());
            }

            if (updateItem.getAvailable() != null) {
                oldItem.setAvailable(updateItem.getAvailable());
            }

        } else {
            throw new NotFoundException(ITEM_NOT_FOUND_EXCEPTION);
        }

        return oldItem;
    }

    @Override
    public void removeItem(Long itemId, Integer userId) {
        Item item = items.get(itemId);

        if (item != null) {
            if (!userStorage.existsById(userId)) {
                throw new NotFoundException(USER_NOT_FOUND_EXCEPTION);
            }

            if (!item.getOwnerId().equals(userId)) {
                throw new AccessDeniedException(ITEM_ACCESS_DENIED_EXCEPTION);
            }

            items.remove(itemId);

        } else {
            throw new NotFoundException(ITEM_NOT_FOUND_EXCEPTION);
        }

    }

    @Override
    public Collection<Item> getAll(Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND_EXCEPTION);
        }

        return items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Item getItem(Long itemId) {
        Item item = items.get(itemId);
        return item;
    }

    @Override
    public Collection<Item> searchItem(String textSearch, Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND_EXCEPTION);
        }

        if (textSearch.isBlank()) {
            return List.of();
        }

        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(textSearch.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(textSearch.toLowerCase()))
                .toList();
    }

    // Генерация идетификатора вещи
    private Long getNextId() {
        Long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
