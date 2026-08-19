package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemResponse;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingResponse mapToBookingResponse(Booking booking) {
        System.out.println("1-----------------" + booking);
        if (booking == null) {
            return null;
        }
        System.out.println("2----------------" + booking);
        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setId(booking.getId());
        bookingResponse.setStart(booking.getStart());
        bookingResponse.setEnd(booking.getEnd());
        bookingResponse.setStatus(booking.getStatus());

        if (booking.getBooker() != null) {
            bookingResponse.setBooker(UserMapper.mapToUserBookingResponse(booking.getBooker()));
        }

        bookingResponse.setItem(ItemMapper.mapToItemBookingResponse(booking.getItem()));

        return bookingResponse;
    }

    public static Booking mapToBooking(NewBookingRequest newBookingRequest) {
        if (newBookingRequest == null) {
            return null;
        }

        Booking booking = new Booking();

        booking.setStart(newBookingRequest.getStart());
        booking.setEnd(newBookingRequest.getEnd());

        Item item = new Item();
        item.setId(newBookingRequest.getItemId());

        booking.setItem(item);

        return booking;
    }

    public static BookingItemResponse mapToBookingItemResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingItemResponse bookingItemResponse = new BookingItemResponse();

        bookingItemResponse.setId(booking.getId());
        bookingItemResponse.setStart(booking.getStart());
        bookingItemResponse.setEnd(booking.getEnd());
        bookingItemResponse.setStatus(booking.getStatus());

        if (booking.getBooker() != null) {
            bookingItemResponse.setBookerId(booking.getBooker().getId());
        }

        return bookingItemResponse;
    }
}
