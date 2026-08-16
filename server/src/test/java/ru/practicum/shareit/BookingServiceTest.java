package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingRequest_shouldReturnBookingResponse_whenValidRequest() {
        Long userId = 1L;
        Long itemId = 1L;
        LocalDateTime now = LocalDateTime.now();

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(now.plusDays(1));
        request.setEnd(now.plusDays(2));

        User booker = new User();
        booker.setId(userId);
        booker.setName("Анна Смирнова");

        Item item = new Item();
        item.setId(itemId);
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(new User());

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setStart(request.getStart());
        savedBooking.setEnd(request.getEnd());
        savedBooking.setBooker(booker);
        savedBooking.setItem(item);
        savedBooking.setStatus(Status.WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBookingRequest(request, userId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.WAITING);

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingRequest_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBookingRequest(request, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingRequest_shouldThrowNotFoundException_whenItemNotFound() {
        Long userId = 1L;
        Long itemId = 99L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);

        User booker = new User();
        booker.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBookingRequest(request, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с данным идентификатором не найдена");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingRequest_shouldThrowItemNotAvailableException_whenItemNotAvailable() {
        Long userId = 1L;
        Long itemId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBookingRequest(request, userId))
                .isInstanceOf(ItemNotAvailableException.class)
                .hasMessage("Запрашиваемая вешь недоступна для брони");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void resolveBooking_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        Long bookingId = 1L;
        Long userId = 2L;
        boolean approve = true;

        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.resolveBooking(bookingId, approve, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("У пользователя нет прав подтверждать бронь");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void resolveBooking_shouldThrowNotFoundException_whenBookingNotFound() {
        Long bookingId = 99L;
        Long userId = 1L;
        boolean approve = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.resolveBooking(bookingId, approve, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронь с данным идентификатором не найдена");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_shouldReturnBooking_whenUserIsOwner() {
        Long bookingId = 1L;
        Long userId = 1L;

        User owner = new User();
        owner.setId(userId);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(bookingId, userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(bookingId);

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingById_shouldReturnBooking_whenUserIsBooker() {
        Long bookingId = 1L;
        Long userId = 2L;

        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(bookingId, userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(bookingId);

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingById_shouldThrowAccessDeniedException_whenUserIsNotOwnerOrBooker() {
        Long bookingId = 1L;
        Long userId = 3L;

        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(bookingId, userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("У пользователя нет прав просматривать бронь");
    }

    @Test
    void getBookingById_shouldThrowNotFoundException_whenBookingNotFound() {
        Long bookingId = 99L;
        Long userId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(bookingId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронь с данным идентификатором не найдена");
    }

    @Test
    void getBookingAllForBooker_shouldReturnAllBookings_whenStateAll() {
        Long userId = 1L;
        String state = "ALL";

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(userId)).thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForBooker(userId, state);

        assertThat(responses).hasSize(1);
        assertThat(responses).extracting(BookingResponse::getId).containsExactly(1L);

        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(userId);
    }

    @Test
    void getBookingAllForBooker_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;
        String state = "ALL";

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingAllForBooker(userId, state))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(bookingRepository, never()).findByBookerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getBookingAllForBooker_shouldReturnBookingsByStatus_whenStateIsStatus() {
        Long userId = 1L;
        String state = "WAITING";

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING)).thenReturn(List.of());

        Collection<BookingResponse> responses = bookingService.getBookingAllForBooker(userId, state);

        assertThat(responses).isEmpty();
        verify(bookingRepository, times(1)).findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
    }

    @Test
    void getBookingAllForOwner_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;
        String state = "ALL";

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingAllForOwner(userId, state))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(bookingRepository, never()).findByItemOwnerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getBookingAllForOwner_shouldThrowNotFoundException_whenUserHasNoItems() {
        Long userId = 1L;
        String state = "ALL";

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsByOwnerId(userId)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingAllForOwner(userId, state))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("У пользователя нет вещей");

        verify(bookingRepository, never()).findByItemOwnerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getBookingAllForOwner_shouldReturnBookingsByStatus_whenStateIsStatus() {
        Long userId = 1L;
        String state = "WAITING";

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.existsByOwnerId(userId)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING)).thenReturn(List.of());

        Collection<BookingResponse> responses = bookingService.getBookingAllForOwner(userId, state);

        assertThat(responses).isEmpty();
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
    }
}
