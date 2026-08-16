package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemResponse mapToItemResponse(Item item) {
        ItemResponse itemResponse = new ItemResponse();

        itemResponse.setId(item.getId());
        itemResponse.setName(item.getName());
        itemResponse.setDescription(item.getDescription());
        itemResponse.setAvailable(item.getAvailable());

        Set<CommentResponse> commentResponse = item.getComments()
                .stream()
                .map(CommentMapper::mapToCommentResponse)
                .collect(Collectors.toSet());
        itemResponse.setComments(commentResponse);

        return itemResponse;
    }

    public static Item mapToItem(NewItemRequest newItemRequest) {
        Item item = new Item();

        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setAvailable(newItemRequest.getAvailable());

        if (newItemRequest.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(newItemRequest.getRequestId());
            item.setRequest(itemRequest);
        }

        return item;
    }

    public static Item mapToItem(UpdateItemPatchRequest updateItemRequest) {
        Item item = new Item();

        item.setName(updateItemRequest.getName());
        item.setDescription(updateItemRequest.getDescription());
        item.setAvailable(updateItemRequest.getAvailable());

        return item;
    }

    public static ItemBookingResponse mapToItemBookingResponse(Item item) {
        ItemBookingResponse itemBookingResponse = new ItemBookingResponse();

        itemBookingResponse.setId(item.getId());
        itemBookingResponse.setName(item.getName());

        return itemBookingResponse;
    }

    public static ItemBookingDateResponse mapToItemBookingDateResponse(Item item, Booking last, Booking next) {
        ItemBookingDateResponse itemBookingResponse = new ItemBookingDateResponse();

        itemBookingResponse.setId(item.getId());
        itemBookingResponse.setName(item.getName());
        itemBookingResponse.setDescription(item.getDescription());
        itemBookingResponse.setAvailable(item.getAvailable());

        Set<CommentResponse> commentResponse = item.getComments()
                .stream()
                .map(CommentMapper::mapToCommentResponse)
                .collect(Collectors.toSet());

        itemBookingResponse.setComments(commentResponse);

        if (last != null) {
            itemBookingResponse.setLastBooking(BookingMapper.mapToBookingItemResponse(last));
        }

        if (next != null) {
            itemBookingResponse.setNextBooking(BookingMapper.mapToBookingItemResponse(next));
        }

        return itemBookingResponse;
    }

    public static ItemForItemRequestResponse mapToItemForItemRequestResponse(Item item) {
        ItemForItemRequestResponse itemForItemRequestResponse = new ItemForItemRequestResponse();

        itemForItemRequestResponse.setId(item.getId());
        itemForItemRequestResponse.setName(item.getName());
        itemForItemRequestResponse.setDescription(item.getDescription());
        itemForItemRequestResponse.setAvailable(item.getAvailable());
        itemForItemRequestResponse.setOwnerId(item.getOwner().getId());

        return itemForItemRequestResponse;
    }
}