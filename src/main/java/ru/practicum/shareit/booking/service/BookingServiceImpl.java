package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public static final String BOOKING_NOT_FOUND_EXCEPTION = "Бронь с данным идентификатором не найдена";
    private static final String BOOKING_ACCESS_APPROV_DENIED_EXCEPTION = "У пользователя нет прав подтверждать бронь";
    private static final String BOOKING_ACCESS_SHOW_DENIED_EXCEPTION = "У пользователя нет прав просматривать бронь";
    private static final String BOOKING_VALID_STATUS_EXCEPTION = "Передан некорректный статус брони";
    private static final String BOOKING_NOT_FOUND_ITEM_FROM_OWNER_EXCEPTION = "У пользователя нет вещей";
    private static final String BOOKING_NOT_AVAILABLE_ITEM_FROM_OWNER_EXCEPTION = "Запрашиваемая вешь " +
            "недоступна для брони";
    private static final String BOOKING_DATE_START_OR_END_IS_BEFORE_NOW_EXCEPTION = "Дата начала или завершения брони" +
            " не могут быть в прошедшем времени";
    private static final String BOOKING_DATE_START_AND_END_IS_EQUALS_EXCEPTION = "Дата начала или завершения брони" +
            " не могут быть равны";

    @Override
    @Transactional
    public BookingResponse createBookingRequest(NewBookingRequest newBookingRequest, Long userId) {
        Booking booking = BookingMapper.mapToBooking(newBookingRequest);

        // Проверка корректности переданных дат
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = booking.getStart().minus(Duration.ofSeconds(5));
        LocalDateTime end = booking.getEnd().minus(Duration.ofSeconds(5));

        if (start.isBefore(now) || end.isBefore(now)) {
            throw new ValidationException(BOOKING_DATE_START_OR_END_IS_BEFORE_NOW_EXCEPTION);
        }

        if (start.equals(end)) {
            throw new ValidationException(BOOKING_DATE_START_AND_END_IS_EQUALS_EXCEPTION);
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION));
        booking.setBooker(booker);

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException(ItemServiceImpl.ITEM_NOT_FOUND_EXCEPTION));

        // Проверка доступна ли вещь к выдаче
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(BOOKING_NOT_AVAILABLE_ITEM_FROM_OWNER_EXCEPTION);
        }

        booking.setItem(item);

        booking.setStatus(Status.WAITING);

        bookingRepository.save(booking);

        return BookingMapper.mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse resolveBooking(Long bookingId, boolean approve, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND_EXCEPTION));

        Long ownerId = booking.getItem().getOwner().getId();

        if (ownerId.equals(userId)) {
            booking.setStatus(approve ? Status.APPROVED : Status.CANCELED);
        } else {
            throw new AccessDeniedException(BOOKING_ACCESS_APPROV_DENIED_EXCEPTION);
        }

        bookingRepository.save(booking);

        return BookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND_EXCEPTION));

        Long owner = booking.getItem().getOwner().getId();

        Long booker = booking.getBooker().getId();

        if (owner.equals(userId) || booker.equals(userId)) {
            return BookingMapper.mapToBookingResponse(booking);
        } else {
            throw new AccessDeniedException(BOOKING_ACCESS_SHOW_DENIED_EXCEPTION);
        }
    }

    @Override
    public Collection<BookingResponse> getBookingAllForBooker(Long userId, String state) {
        StatusRequest statusRequest;

        try {
            statusRequest = StatusRequest.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidationException(BOOKING_VALID_STATUS_EXCEPTION + " " + state);
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        switch (statusRequest) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            case CURRENT:
                return bookingRepository.findCurrentByBookerId(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            case PAST:
                return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            default:
                Status status = Status.valueOf(state.toUpperCase());

                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, status)
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();
        }
    }

    @Override
    public Collection<BookingResponse> getBookingAllForOwner(Long userId, String state) {
        StatusRequest statusRequest;

        try {
            statusRequest = StatusRequest.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidationException(BOOKING_VALID_STATUS_EXCEPTION + " " + state);
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserServiceImpl.USER_NOT_FOUND_EXCEPTION);
        }

        if (!itemRepository.existsByOwnerId(userId)) {
            throw new NotFoundException(BOOKING_NOT_FOUND_ITEM_FROM_OWNER_EXCEPTION);
        }

        switch (statusRequest) {
            case ALL:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId)
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            case CURRENT:
                return bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();

            default:
                Status status = Status.valueOf(state.toUpperCase());

                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, status)
                        .stream()
                        .map(BookingMapper::mapToBookingResponse)
                        .toList();
        }
    }
}
