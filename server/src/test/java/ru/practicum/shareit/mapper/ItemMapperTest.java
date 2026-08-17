package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void mapToItemResponse_shouldMapAllFields() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Анна Смирнова");

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("Отличная вещь!");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(owner);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComments(Set.of(comment));

        ItemResponse response = ItemMapper.mapToItemResponse(item);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Дрель");
        assertThat(response.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(response.getAvailable()).isTrue();
        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getComments()).extracting(CommentResponse::getText)
                .containsExactly("Отличная вещь!");
    }

    @Test
    void mapToItemResponse_shouldReturnNull_whenItemIsNull() {
        ItemResponse response = ItemMapper.mapToItemResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToItemResponse_shouldHandleEmptyComments() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setComments(Set.of());

        ItemResponse response = ItemMapper.mapToItemResponse(item);

        assertThat(response).isNotNull();
        assertThat(response.getComments()).isEmpty();
    }

    @Test
    void mapToItem_fromNewItemRequest_shouldMapAllFields() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);
        request.setRequestId(5L);

        Item item = ItemMapper.mapToItem(request);

        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequest()).isNotNull();
        assertThat(item.getRequest().getId()).isEqualTo(5L);
    }

    @Test
    void mapToItem_fromNewItemRequest_shouldHandleNullRequestId() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);

        Item item = ItemMapper.mapToItem(request);

        assertThat(item).isNotNull();
        assertThat(item.getRequest()).isNull();
    }

    @Test
    void mapToItem_fromNewItemRequest_shouldHandleNullRequest() {
        Item item = ItemMapper.mapToItem((NewItemRequest) null);

        assertThat(item).isNull();
    }

    @Test
    void mapToItem_fromUpdateItemPatchRequest_shouldMapAllFields() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Обновленная дрель");
        request.setDescription("Обновленное описание");
        request.setAvailable(false);

        Item item = ItemMapper.mapToItem(request);

        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("Обновленная дрель");
        assertThat(item.getDescription()).isEqualTo("Обновленное описание");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void mapToItem_fromUpdateItemPatchRequest_shouldHandleNullFields() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();

        Item item = ItemMapper.mapToItem(request);

        assertThat(item).isNotNull();
        assertThat(item.getName()).isNull();
        assertThat(item.getDescription()).isNull();
        assertThat(item.getAvailable()).isNull();
    }

    @Test
    void mapToItem_fromUpdateItemPatchRequest_shouldHandleNullRequest() {
        Item item = ItemMapper.mapToItem((UpdateItemPatchRequest) null);

        assertThat(item).isNull();
    }

    @Test
    void mapToItemBookingResponse_shouldMapIdAndName() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);

        ItemBookingResponse response = ItemMapper.mapToItemBookingResponse(item);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Дрель");
    }

    @Test
    void mapToItemBookingResponse_shouldReturnNull_whenItemIsNull() {
        ItemBookingResponse response = ItemMapper.mapToItemBookingResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToItemBookingDateResponse_shouldMapAllFieldsWithoutBookings() {
        User owner = new User();
        owner.setId(1L);

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("Отличная вещь!");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(owner);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComments(Set.of(comment));

        ItemBookingDateResponse response = ItemMapper.mapToItemBookingDateResponse(item, null, null);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Дрель");
        assertThat(response.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(response.getAvailable()).isTrue();
        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getLastBooking()).isNull();
        assertThat(response.getNextBooking()).isNull();
    }

    @Test
    void mapToItemBookingDateResponse_shouldMapWithBookings() {
        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComments(Set.of());

        Booking last = new Booking();
        last.setId(10L);
        last.setBooker(booker);

        Booking next = new Booking();
        next.setId(11L);
        next.setBooker(booker);

        ItemBookingDateResponse response = ItemMapper.mapToItemBookingDateResponse(item, last, next);

        assertThat(response).isNotNull();
        assertThat(response.getLastBooking()).isNotNull();
        assertThat(response.getLastBooking().getId()).isEqualTo(10L);
        assertThat(response.getNextBooking()).isNotNull();
        assertThat(response.getNextBooking().getId()).isEqualTo(11L);
    }

    @Test
    void mapToItemBookingDateResponse_shouldReturnNull_whenItemIsNull() {
        ItemBookingDateResponse response = ItemMapper.mapToItemBookingDateResponse(null, null, null);

        assertThat(response).isNull();
    }

    @Test
    void mapToItemForItemRequestResponse_shouldMapAllFields() {
        User owner = new User();
        owner.setId(5L);
        owner.setName("Анна Смирнова");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemForItemRequestResponse response = ItemMapper.mapToItemForItemRequestResponse(item);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Дрель");
        assertThat(response.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(response.getAvailable()).isTrue();
        assertThat(response.getOwnerId()).isEqualTo(5L);
    }

    @Test
    void mapToItemForItemRequestResponse_shouldReturnNull_whenItemIsNull() {
        ItemForItemRequestResponse response = ItemMapper.mapToItemForItemRequestResponse(null);

        assertThat(response).isNull();
    }
}