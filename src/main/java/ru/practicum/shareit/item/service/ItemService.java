package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    public Collection<ItemBookingDateResponse> getAll(Long userId);

    public ItemResponse getItem(Long itemId);

    public Collection<ItemResponse> searchItem(String text, Long userId);

    public ItemResponse createItem(NewItemRequest newItem, Long userId);

    public ItemResponse updateItem(Long itemId, UpdateItemPatchRequest updateItem, Long userId);

    public void removeItem(Long itemId, Long userId);

    public CommentResponse addComment(NewCommentRequest newCommentRequest, Long itemId, Long userId);
}
