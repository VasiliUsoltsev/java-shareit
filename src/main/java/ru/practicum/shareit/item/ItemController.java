package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemBookingDateResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemBookingDateResponse get(@PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemResponse> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam String text
    ) {
        return itemService.searchItem(text, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@Valid @RequestBody NewItemRequest newItem,
                               @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.createItem(newItem, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addCommaent(@Valid @RequestBody NewCommentRequest newCommentRequest,
                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId
    ) {
        return itemService.addComment(newCommentRequest, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@Valid @RequestBody UpdateItemPatchRequest updateItem,
                               @PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.debug("Пришел запрос на апдейт");
        return itemService.updateItem(itemId, updateItem, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable() Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        itemService.removeItem(itemId, userId);
    }
}
