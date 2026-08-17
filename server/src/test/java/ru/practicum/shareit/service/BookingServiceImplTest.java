package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private NewBookingRequest newBookingRequest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Анна Смирнова");

        owner = new User();
        owner.setId(2L);
        owner.setName("Иван Петров");

        item = new Item();
        item.setId(10L);
        item.setName("Дрель");
        item.setAvailable(true);
        item.setOwner(owner);

        newBookingRequest = new NewBookingRequest();
        newBookingRequest.setItemId(10L);
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(100L);
        booking.setStart(newBookingRequest.getStart());
        booking.setEnd(newBookingRequest.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
    }

    @Test
    void createBookingRequest_shouldReturnBookingResponse_whenValid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.createBookingRequest(newBookingRequest, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo(Status.WAITING);
        assertThat(response.getBooker().getId()).isEqualTo(1L);
        assertThat(response.getItem().getId()).isEqualTo(10L);

        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findById(10L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingRequest_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBookingRequest(newBookingRequest, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingRequest_shouldThrowNotFoundException_whenItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBookingRequest(newBookingRequest, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с данным идентификатором не найдена");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingRequest_shouldThrowItemNotAvailableException_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBookingRequest(newBookingRequest, 1L))
                .isInstanceOf(ItemNotAvailableException.class)
                .hasMessage("Запрашиваемая вешь недоступна для брони");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void resolveBooking_shouldApproveBooking_whenUserIsOwner() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.resolveBooking(100L, true, 2L);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.APPROVED);

        verify(bookingRepository, times(1)).findById(100L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void resolveBooking_shouldCancelBooking_whenUserIsOwnerAndApproveFalse() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.resolveBooking(100L, false, 2L);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.CANCELED);

        verify(bookingRepository, times(1)).findById(100L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void resolveBooking_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.resolveBooking(100L, true, 3L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("У пользователя нет прав подтверждать бронь");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void resolveBooking_shouldThrowNotFoundException_whenBookingNotFound() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.resolveBooking(100L, true, 2L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронь с данным идентификатором не найдена");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_shouldReturnBooking_whenUserIsOwner() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(100L, 2L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);

        verify(bookingRepository, times(1)).findById(100L);
    }

    @Test
    void getBookingById_shouldReturnBooking_whenUserIsBooker() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingResponse response = bookingService.getBookingById(100L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);

        verify(bookingRepository, times(1)).findById(100L);
    }

    @Test
    void getBookingById_shouldThrowAccessDeniedException_whenUserIsNotOwnerOrBooker() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(100L, 3L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("У пользователя нет прав просматривать бронь");

        verify(bookingRepository, times(1)).findById(100L);
    }

    @Test
    void getBookingById_shouldThrowNotFoundException_whenBookingNotFound() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(100L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронь с данным идентификатором не найдена");

        verify(bookingRepository, times(1)).findById(100L);
    }

    @Test
    void getBookingAllForBooker_shouldReturnAllBookings_whenStateAll() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForBooker(1L, "ALL");

        assertThat(responses).hasSize(1);
        assertThat(responses).extracting(BookingResponse::getId).containsExactly(100L);

        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(1L);
    }

    @Test
    void getBookingAllForBooker_shouldReturnCurrentBookings_whenStateCurrent() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findCurrentByBookerId(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForBooker(1L, "CURRENT");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1)).findCurrentByBookerId(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getBookingAllForBooker_shouldReturnPastBookings_whenStatePast() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForBooker(1L, "PAST");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndBeforeOrderByStartDesc(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getBookingAllForBooker_shouldReturnFutureBookings_whenStateFuture() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForBooker(1L, "FUTURE");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartAfterOrderByStartDesc(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getBookingAllForBooker_shouldReturnBookingsByStatus_whenStateIsStatus() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, Status.WAITING))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForBooker(1L, "WAITING");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(1L, Status.WAITING);
    }

    @Test
    void getBookingAllForBooker_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingAllForBooker(1L, "ALL"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(bookingRepository, never()).findByBookerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getBookingAllForOwner_shouldReturnAllBookings_whenStateAll() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsByOwnerId(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(2L)).thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForOwner(2L, "ALL");

        assertThat(responses).hasSize(1);
        assertThat(responses).extracting(BookingResponse::getId).containsExactly(100L);

        verify(bookingRepository, times(1)).findByItemOwnerIdOrderByStartDesc(2L);
    }

    @Test
    void getBookingAllForOwner_shouldReturnCurrentBookings_whenStateCurrent() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsByOwnerId(2L)).thenReturn(true);
        when(bookingRepository.findCurrentByOwnerId(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForOwner(2L, "CURRENT");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1)).findCurrentByOwnerId(eq(2L), any(LocalDateTime.class));
    }

    @Test
    void getBookingAllForOwner_shouldReturnPastBookings_whenStatePast() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsByOwnerId(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForOwner(2L, "PAST");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(2L), any(LocalDateTime.class));
    }

    @Test
    void getBookingAllForOwner_shouldReturnFutureBookings_whenStateFuture() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsByOwnerId(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForOwner(2L, "FUTURE");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(2L), any(LocalDateTime.class));
    }

    @Test
    void getBookingAllForOwner_shouldReturnBookingsByStatus_whenStateIsStatus() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsByOwnerId(2L)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L, Status.WAITING))
                .thenReturn(List.of(booking));

        Collection<BookingResponse> responses = bookingService.getBookingAllForOwner(2L, "WAITING");

        assertThat(responses).hasSize(1);
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(2L, Status.WAITING);
    }

    @Test
    void getBookingAllForOwner_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingAllForOwner(2L, "ALL"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(bookingRepository, never()).findByItemOwnerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getBookingAllForOwner_shouldThrowNotFoundException_whenUserHasNoItems() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsByOwnerId(2L)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getBookingAllForOwner(2L, "ALL"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("У пользователя нет вещей");

        verify(bookingRepository, never()).findByItemOwnerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getBookingAllForBooker_shouldThrowIllegalArgumentException_whenStateIsInvalid() {
        assertThatThrownBy(() -> bookingService.getBookingAllForBooker(1L, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bookingRepository, never()).findByBookerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getBookingAllForOwner_shouldThrowIllegalArgumentException_whenStateIsInvalid() {
        assertThatThrownBy(() -> bookingService.getBookingAllForOwner(2L, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(bookingRepository, never()).findByItemOwnerIdOrderByStartDesc(anyLong());
    }
}