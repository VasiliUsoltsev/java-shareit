package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getAll(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@PathVariable Long itemId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam String text
    ) {
        return itemClient.searchItem(text, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody NewItemRequest newItem,
                                         @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemClient.createItem(newItem, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addCommaent(@Valid @RequestBody NewCommentRequest newCommentRequest,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId
    ) {
        return itemClient.addComment(newCommentRequest, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateItemPatchRequest updateItem,
                                         @PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemClient.updateItem(itemId, updateItem, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable() Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        itemClient.removeItem(itemId, userId);
    }
}
