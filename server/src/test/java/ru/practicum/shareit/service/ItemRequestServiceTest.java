package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestBaseResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequest_shouldReturnBaseResponse_whenValidRequest() {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна аккумуляторная дрель");

        User requestor = new User();
        requestor.setId(userId);
        requestor.setName("Анна Смирнова");

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setId(1L);
        savedRequest.setDescription("Нужна аккумуляторная дрель");
        savedRequest.setRequestor(requestor);
        savedRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ItemRequestBaseResponse response = itemRequestService.createItemRequest(request, userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDescription()).isEqualTo("Нужна аккумуляторная дрель");
        assertThat(response.getCreated()).isNotNull();

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequest_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна дрель");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.createItemRequest(request, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getAllItemRequestsRequester_shouldReturnRequests_whenUserExists() {
        Long userId = 1L;
        User requestor = new User();
        requestor.setId(userId);
        requestor.setName("Анна Смирнова");

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Нужна дрель");
        request1.setRequestor(requestor);
        request1.setCreated(LocalDateTime.now().minusDays(1));

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Нужна отвертка");
        request2.setRequestor(requestor);
        request2.setCreated(LocalDateTime.now());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(request1, request2));

        Collection<ItemRequestWithAnswersResponse> responses = itemRequestService.getAllItemRequestsRequester(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(ItemRequestWithAnswersResponse::getDescription)
                .containsExactly("Нужна дрель", "Нужна отвертка");

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);
    }

    @Test
    void getAllItemRequestsRequester_shouldReturnEmptyList_whenNoRequests() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(List.of());

        Collection<ItemRequestWithAnswersResponse> responses = itemRequestService.getAllItemRequestsRequester(userId);

        assertThat(responses).isEmpty();
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);
    }

    @Test
    void getAllItemRequestsRequester_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> itemRequestService.getAllItemRequestsRequester(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(itemRequestRepository, never()).findByRequestorIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAll_shouldReturnAllRequestsExceptOwn_whenUserExists() {
        Long userId = 1L;
        User requestor = new User();
        requestor.setId(userId);

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Иван Петров");

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Нужна дрель");
        request1.setRequestor(otherUser);
        request1.setCreated(LocalDateTime.now());

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Нужна отвертка");
        request2.setRequestor(otherUser);
        request2.setCreated(LocalDateTime.now().minusDays(1));

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of(request1, request2));

        Collection<ItemRequestBaseResponse> responses = itemRequestService.getAll(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(ItemRequestBaseResponse::getDescription)
                .containsExactly("Нужна дрель", "Нужна отвертка");

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findByRequestorIdNotOrderByCreatedDesc(userId);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoOtherRequests() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)).thenReturn(List.of());

        Collection<ItemRequestBaseResponse> responses = itemRequestService.getAll(userId);

        assertThat(responses).isEmpty();
        verify(itemRequestRepository, times(1)).findByRequestorIdNotOrderByCreatedDesc(userId);
    }

    @Test
    void getAll_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> itemRequestService.getAll(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(itemRequestRepository, never()).findByRequestorIdNotOrderByCreatedDesc(anyLong());
    }

    @Test
    void getItemRequest_shouldReturnWithAnswers_whenExists() {
        Long requestId = 1L;
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("Анна Смирнова");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Нужна аккумуляторная дрель");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestWithAnswersResponse response = itemRequestService.getItemRequest(requestId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(requestId);
        assertThat(response.getDescription()).isEqualTo("Нужна аккумуляторная дрель");
        assertThat(response.getItems()).isNotNull();
        assertThat(response.getItems()).isEmpty();

        verify(itemRequestRepository, times(1)).findById(requestId);
    }

    @Test
    void getItemRequest_shouldThrowNotFoundException_whenRequestNotFound() {
        Long requestId = 99L;

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequest(requestId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Запрос с данным идентификатором не найден");

        verify(itemRequestRepository, times(1)).findById(requestId);
    }
}
