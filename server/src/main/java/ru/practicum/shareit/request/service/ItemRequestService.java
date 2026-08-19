package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestBaseResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    public ItemRequestBaseResponse createItemRequest(NewItemRequest newItemRequest, Long userId);

    public Collection<ItemRequestWithAnswersResponse> getAllItemRequestsRequester(Long userId);

    public Collection<ItemRequestBaseResponse> getAll(Long userId);

    public ItemRequestWithAnswersResponse getItemRequest(Long itemRequestId);
}