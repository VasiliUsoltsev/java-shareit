package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestBaseResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    @Test
    void mapToItemRequestBaseResponse_shouldMapAllFields() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("Анна Смирнова");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Нужна аккумуляторная дрель");
        itemRequest.setCreated(LocalDateTime.of(2026, 8, 17, 12, 0));
        itemRequest.setRequestor(requestor);

        ItemRequestBaseResponse response = ItemRequestMapper.mapToItemRequestBaseResponse(itemRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getDescription()).isEqualTo("Нужна аккумуляторная дрель");
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2026, 8, 17, 12, 0));
    }

    @Test
    void mapToItemRequestBaseResponse_shouldHandleNullRequest() {
        ItemRequestBaseResponse response = ItemRequestMapper.mapToItemRequestBaseResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToItemRequest_shouldMapNewItemRequest() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна отвертка");

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(request);

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getDescription()).isEqualTo("Нужна отвертка");
    }

    @Test
    void mapToItemRequest_shouldHandleNullRequest() {
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(null);

        assertThat(itemRequest).isNull();
    }

    @Test
    void mapToItemRequest_shouldHandleEmptyDescription() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("");

        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(request);

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getDescription()).isEmpty();
    }

    @Test
    void mapToItemRequestWithAnswersResponse_shouldHandleNullItems() {
        User requestor = new User();
        requestor.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Нужны инструменты");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(requestor);
        itemRequest.setItems(null);

        ItemRequestWithAnswersResponse response = ItemRequestMapper.mapToItemRequestWithAnswersResponse(itemRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getItems()).isNull();
    }

    @Test
    void mapToItemRequestWithAnswersResponse_shouldHandleEmptyItems() {
        User requestor = new User();
        requestor.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setDescription("Нужны инструменты");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(requestor);
        itemRequest.setItems(List.of());

        ItemRequestWithAnswersResponse response = ItemRequestMapper.mapToItemRequestWithAnswersResponse(itemRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void mapToItemRequestWithAnswersResponse_shouldHandleNullRequest() {
        ItemRequestWithAnswersResponse response = ItemRequestMapper.mapToItemRequestWithAnswersResponse(null);

        assertThat(response).isNull();
    }
}
