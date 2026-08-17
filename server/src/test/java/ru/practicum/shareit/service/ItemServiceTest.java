package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> itemService.createItem(request, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createItem_shouldSetRequest_whenRequestIdProvided() {
        Long userId = 1L;
        Long requestId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);
        request.setRequestId(requestId);

        User owner = new User();
        owner.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Нужна дрель");

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Дрель");
        savedItem.setRequest(itemRequest);
        savedItem.setOwner(owner);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemResponse response = itemService.createItem(request, userId);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Дрель");

        verify(itemRequestRepository, times(1)).findById(requestId);
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenRequestNotFound() {
        Long userId = 1L;
        Long requestId = 99L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setRequestId(requestId);

        User owner = new User();
        owner.setId(userId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(request, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Запрос с данным идентификатором не найден");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getAll_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> itemService.getAll(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(itemRepository, never()).findByOwnerId(anyLong());
    }

    @Test
    void getItem_shouldReturnItemWithoutBookings_whenUserIsNotOwner() {
        Long itemId = 1L;
        Long userId = 2L;

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Дрель");
        item.setOwner(owner);

        when(itemRepository.findByIdWithComments(itemId)).thenReturn(Optional.of(item));

        ItemBookingDateResponse response = itemService.getItem(itemId, userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(itemId);
        assertThat(response.getName()).isEqualTo("Дрель");
        assertThat(response.getLastBooking()).isNull();
        assertThat(response.getNextBooking()).isNull();

        verify(bookingRepository, never()).findLastBookingByOwnerAndItem(anyLong(), anyLong(), any());
        verify(bookingRepository, never()).findNextBookingByOwnerAndItem(anyLong(), anyLong(), any());
    }

    @Test
    void getItem_shouldThrowNotFoundException_whenItemNotFound() {
        Long itemId = 99L;
        Long userId = 1L;

        when(itemRepository.findByIdWithComments(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(itemId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с данным идентификатором не найдена");
    }

    @Test
    void updateItem_shouldUpdateFields_whenValidRequest() {
        Long itemId = 1L;
        Long userId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item oldItem = new Item();
        oldItem.setId(itemId);
        oldItem.setName("Старая дрель");
        oldItem.setDescription("Старое описание");
        oldItem.setAvailable(false);
        oldItem.setOwner(owner);

        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Новая дрель");
        request.setDescription("Новое описание");
        request.setAvailable(true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(any(Item.class))).thenReturn(oldItem);

        ItemResponse response = itemService.updateItem(itemId, request, userId);

        assertThat(response.getName()).isEqualTo("Новая дрель");
        assertThat(response.getDescription()).isEqualTo("Новое описание");
        assertThat(response.getAvailable()).isTrue();

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(oldItem);
    }

    @Test
    void updateItem_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        Long itemId = 1L;
        Long userId = 2L;

        User owner = new User();
        owner.setId(1L);

        Item oldItem = new Item();
        oldItem.setId(itemId);
        oldItem.setName("Дрель");
        oldItem.setOwner(owner);

        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Новая дрель");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        assertThatThrownBy(() -> itemService.updateItem(itemId, request, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("У пользователя нет прав редактировать данную вещь");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemNotFound() {
        Long itemId = 99L;
        Long userId = 1L;
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Новая дрель");

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(itemId, request, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с данным идентификатором не найдена");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void searchItem_shouldReturnItems_whenTextProvided() {
        Long userId = 1L;
        String text = "дрель";

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.searchByText(text)).thenReturn(List.of(item));

        Collection<ItemResponse> responses = itemService.searchItem(text, userId);

        assertThat(responses).hasSize(1);
        assertThat(responses).extracting(ItemResponse::getName)
                .containsExactly("Дрель");

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRepository, times(1)).searchByText(text);
    }

    @Test
    void searchItem_shouldReturnEmptyList_whenTextIsBlank() {
        Long userId = 1L;
        String text = "   ";

        when(userRepository.existsById(userId)).thenReturn(true);

        Collection<ItemResponse> responses = itemService.searchItem(text, userId);

        assertThat(responses).isEmpty();
        verify(itemRepository, never()).searchByText(anyString());
    }

    @Test
    void searchItem_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;
        String text = "дрель";

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> itemService.searchItem(text, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(itemRepository, never()).searchByText(anyString());
    }

    @Test
    void removeItem_shouldDeleteItem_whenUserIsOwner() {
        Long itemId = 1L;
        Long userId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Дрель");
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);

        itemService.removeItem(itemId, userId);

        verify(itemRepository, times(1)).findById(itemId);
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void removeItem_shouldThrowNotFoundException_whenItemNotFound() {
        Long itemId = 99L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.removeItem(itemId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с данным идентификатором не найдена");

        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    void removeItem_shouldThrowNotFoundException_whenUserNotFound() {
        Long itemId = 1L;
        Long userId = 99L;

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> itemService.removeItem(itemId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    void removeItem_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        Long itemId = 1L;
        Long userId = 2L;

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThatThrownBy(() -> itemService.removeItem(itemId, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("У пользователя нет прав редактировать данную вещь");

        verify(itemRepository, never()).delete(any(Item.class));
    }
}
