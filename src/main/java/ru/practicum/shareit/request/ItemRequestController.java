package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestBaseResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public Collection<ItemRequestWithAnswersResponse> getAllItemRequestsRequester(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemRequestService.getAllItemRequestsRequester(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestBaseResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersResponse getItemRequest(@PathVariable Long requestId) {
        return itemRequestService.getItemRequest(requestId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestBaseResponse createItemRequest(@Valid @RequestBody NewItemRequest newItemRequest,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemRequestService.createItemRequest(newItemRequest, userId);
    }
}
