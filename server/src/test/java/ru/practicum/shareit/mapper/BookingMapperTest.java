package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingItemResponse;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void mapToBookingResponse_shouldMapAllFields() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Анна Смирнова");

        User owner = new User();
        owner.setId(2L);
        owner.setName("Иван Петров");

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2026, 8, 17, 12, 0));
        booking.setEnd(LocalDateTime.of(2026, 8, 18, 12, 0));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        BookingResponse response = BookingMapper.mapToBookingResponse(booking);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 17, 12, 0));
        assertThat(response.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 18, 12, 0));
        assertThat(response.getStatus()).isEqualTo(Status.WAITING);
        assertThat(response.getBooker()).isNotNull();
        assertThat(response.getBooker().getId()).isEqualTo(1L);
        assertThat(response.getItem()).isNotNull();
        assertThat(response.getItem().getId()).isEqualTo(10L);
        assertThat(response.getItem().getName()).isEqualTo("Дрель");
    }

    @Test
    void mapToBookingResponse_shouldReturnNull_whenBookingIsNull() {
        BookingResponse response = BookingMapper.mapToBookingResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToBookingResponse_shouldHandleNullBooker() {
        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(null);
        booking.setItem(item);

        BookingResponse response = BookingMapper.mapToBookingResponse(booking);

        assertThat(response).isNotNull();
        assertThat(response.getBooker()).isNull();
    }

    @Test
    void mapToBookingResponse_shouldHandleNullItem() {
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(null);

        BookingResponse response = BookingMapper.mapToBookingResponse(booking);

        assertThat(response).isNotNull();
        assertThat(response.getItem()).isNull();
    }

    @Test
    void mapToBooking_shouldMapNewBookingRequest() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(10L);
        request.setStart(LocalDateTime.of(2026, 8, 17, 12, 0));
        request.setEnd(LocalDateTime.of(2026, 8, 18, 12, 0));

        Booking booking = BookingMapper.mapToBooking(request);

        assertThat(booking).isNotNull();
        assertThat(booking.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 17, 12, 0));
        assertThat(booking.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 18, 12, 0));
        assertThat(booking.getItem()).isNotNull();
        assertThat(booking.getItem().getId()).isEqualTo(10L);
    }

    @Test
    void mapToBooking_shouldReturnNull_whenRequestIsNull() {
        Booking booking = BookingMapper.mapToBooking(null);

        assertThat(booking).isNull();
    }

    @Test
    void mapToBooking_shouldHandleNullFields() {
        NewBookingRequest request = new NewBookingRequest();

        Booking booking = BookingMapper.mapToBooking(request);

        assertThat(booking).isNotNull();
        assertThat(booking.getStart()).isNull();
        assertThat(booking.getEnd()).isNull();
        assertThat(booking.getItem()).isNotNull();
        assertThat(booking.getItem().getId()).isNull();
    }

    @Test
    void mapToBookingItemResponse_shouldMapAllFields() {
        User booker = new User();
        booker.setId(5L);
        booker.setName("Анна Смирнова");

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2026, 8, 17, 12, 0));
        booking.setEnd(LocalDateTime.of(2026, 8, 18, 12, 0));
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);

        BookingItemResponse response = BookingMapper.mapToBookingItemResponse(booking);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 17, 12, 0));
        assertThat(response.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 18, 12, 0));
        assertThat(response.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(response.getBookerId()).isEqualTo(5L);
    }

    @Test
    void mapToBookingItemResponse_shouldReturnNull_whenBookingIsNull() {
        BookingItemResponse response = BookingMapper.mapToBookingItemResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToBookingItemResponse_shouldHandleNullBooker() {
        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(null);

        BookingItemResponse response = BookingMapper.mapToBookingItemResponse(booking);

        assertThat(response).isNotNull();
        assertThat(response.getBookerId()).isNull();
    }
}