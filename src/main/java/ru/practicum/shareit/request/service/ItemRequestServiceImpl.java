package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestBaseResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public static final String ITEMREQUEST_NOT_FOUNT_EXCEPTION = "Запрос с данным идентификатором не найден";


    @Override
    @Transactional
    public ItemRequestBaseResponse createItemRequest(NewItemRequest newItemRequest, Long userId) {
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(newItemRequest);

        User requestor = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION));
        itemRequest.setRequestor(requestor);

        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestBaseResponse(itemRequest);
    }

    @Override
    public Collection<ItemRequestWithAnswersResponse> getAllItemRequestsRequester(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestWithAnswersResponse)
                .toList();
    }

    @Override
    public Collection<ItemRequestBaseResponse> getAll(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestBaseResponse)
                .toList();
    }

    @Override
    public ItemRequestWithAnswersResponse getItemRequest(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(ITEMREQUEST_NOT_FOUNT_EXCEPTION));

        return ItemRequestMapper.mapToItemRequestWithAnswersResponse(itemRequest);
    }
}
