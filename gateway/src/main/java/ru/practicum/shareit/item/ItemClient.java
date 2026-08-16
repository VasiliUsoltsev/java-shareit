package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAll(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItem(String text, Long userId) {
        Map<String, Object> parameters = Map.of("text", text);

        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createItem(NewItemRequest newItem, Long userId) {
        return post("", userId, newItem);
    }

    public ResponseEntity<Object> updateItem(Long itemId, UpdateItemPatchRequest updateItem, Long userId) {
        return patch("/" + itemId, userId, updateItem);
    }

    public void removeItem(Long itemId, Long userId) {
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> addComment(NewCommentRequest newCommentRequest, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, newCommentRequest);
    }
}
