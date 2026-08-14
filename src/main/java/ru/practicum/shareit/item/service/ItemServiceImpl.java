package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public static final String ITEM_NOT_FOUND_EXCEPTION = "Вещь с данным идентификатором не найдена";
    private static final String ITEM_ACCESS_DENIED_EXCEPTION = "У пользователя нет прав редактировать данную вещь";


    @Override
    public Collection<ItemBookingDateResponse> getAll(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        LocalDateTime now = LocalDateTime.now();

        // Собираем вещи владельца()
        List<Item> items = itemRepository.findByOwnerId(userId);

        // Собираем брони подходящие под условия
        List<Booking> lastBookings = bookingRepository.findAllLastBookingsByOwnerId(userId, now);
        List<Booking> nextBookings = bookingRepository.findAllNextBookingsByOwnerId(userId, now);

        // Организуем списки в Map для быстрого доступа
        Map<Long, Booking> lastBookingMap = lastBookings
                .stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b));

        Map<Long, Booking> nextBookingMap = nextBookings
                .stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b));

        // Обогащаем вещи нужными данными
        return items.stream()
                .map(item -> {
                    Booking last = lastBookingMap.get(item.getId());
                    Booking next = nextBookingMap.get(item.getId());

                    return ItemMapper.mapToItemBookingDateResponse(item, last, next);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBookingDateResponse getItem(Long itemId, Long userId) {
        Item item = itemRepository.findByIdWithComments(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_EXCEPTION));

        boolean isOwner = item.getOwner().getId().equals(userId);
        LocalDateTime now = LocalDateTime.now();

        Booking last = null;
        Booking next = null;

        if (isOwner) {
            last = bookingRepository.findLastBookingByOwnerAndItem(userId, itemId, now)
                    .orElse(null);
            next = bookingRepository.findNextBookingByOwnerAndItem(userId, itemId, now)
                    .orElse(null);
        }

        return ItemMapper.mapToItemBookingDateResponse(item, last, next);
    }

    @Override
    public Collection<ItemResponse> searchItem(String textSearch, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        if (textSearch.isBlank()) {
            return List.of();
        }

        return itemRepository.searchByText(textSearch)
                .stream()
                .map(ItemMapper::mapToItemResponse)
                .toList();
    }

    @Override
    @Transactional
    public ItemResponse createItem(NewItemRequest newItem, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        Item item = ItemMapper.mapToItem(newItem);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION));

        item.setOwner(owner);

        itemRepository.save(item);

        return ItemMapper.mapToItemResponse(item);
    }

    @Override
    @Transactional
    public ItemResponse updateItem(Long itemId, UpdateItemPatchRequest updateItemRequest, Long userId) {
        Item updateItem = ItemMapper.mapToItem(updateItemRequest);

        updateItem.setId(itemId);

        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_EXCEPTION));

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(ITEM_ACCESS_DENIED_EXCEPTION);
        }

        if (updateItem.getName() != null) {
            oldItem.setName(updateItem.getName());
        }

        if (updateItem.getDescription() != null) {
            oldItem.setDescription(updateItem.getDescription());
        }

        if (updateItem.getAvailable() != null) {
            oldItem.setAvailable(updateItem.getAvailable());
        }

        itemRepository.save(oldItem);

        return ItemMapper.mapToItemResponse(oldItem);
    }

    @Override
    public void removeItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_EXCEPTION));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(ITEM_ACCESS_DENIED_EXCEPTION);
        }

        itemRepository.delete(item);
    }

    @Override
    @Transactional
    public CommentResponse addComment(NewCommentRequest newCommentRequest, Long itemId, Long userId) {
        Comment comment = CommentMapper.mapToComment(newCommentRequest);

        if (!bookingRepository.existsCompletedBookingByUserAndItem(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException("Пользователь не бронировал данную вещь");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION));
        comment.setAuthor(author);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ItemServiceImpl.ITEM_NOT_FOUND_EXCEPTION));
        comment.setItem(item);

        comment = commentRepository.save(comment);

        return CommentMapper.mapToCommentResponse(comment);
    }
}
