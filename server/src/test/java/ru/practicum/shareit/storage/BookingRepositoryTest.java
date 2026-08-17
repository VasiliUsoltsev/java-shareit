package ru.practicum.shareit.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        owner = new User();
        owner.setName("Анна Смирнова");
        owner.setEmail("anna@mail.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Иван Петров");
        booker.setEmail("ivan@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void save_shouldSaveBooking() {
        Booking booking = new Booking();
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        Booking saved = bookingRepository.save(booking);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStart()).isEqualTo(now.plusDays(1));
        assertThat(saved.getEnd()).isEqualTo(now.plusDays(2));
        assertThat(saved.getItem().getId()).isEqualTo(item.getId());
        assertThat(saved.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(saved.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void findById_shouldReturnBooking_whenExists() {
        Booking booking = new Booking();
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        Optional<Booking> found = bookingRepository.findById(booking.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc_shouldReturnBookings() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking booking2 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));
        Booking booking3 = createBooking(booker, Status.APPROVED, now.plusDays(5), now.plusDays(6));

        List<Booking> bookings = bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.plusDays(3));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.plusDays(1));
    }

    @Test
    void findByBookerIdOrderByStartDesc_shouldReturnAllBookings() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking booking2 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.plusDays(3));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.plusDays(1));
    }

    @Test
    void findCurrentByBookerId_shouldReturnCurrentBookings() {
        Booking past = createBooking(booker, Status.WAITING, now.minusDays(3), now.minusDays(2));
        Booking current = createBooking(booker, Status.WAITING, now.minusHours(1), now.plusHours(2));
        Booking future = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> bookings = bookingRepository.findCurrentByBookerId(booker.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(current.getId());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc_shouldReturnPastBookings() {
        Booking past1 = createBooking(booker, Status.WAITING, now.minusDays(5), now.minusDays(3));
        Booking past2 = createBooking(booker, Status.WAITING, now.minusDays(4), now.minusDays(2));
        Booking future = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> bookings = bookingRepository
                .findByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), now);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.minusDays(4));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.minusDays(5));
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_shouldReturnFutureBookings() {
        Booking past = createBooking(booker, Status.WAITING, now.minusDays(3), now.minusDays(2));
        Booking future1 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking future2 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));

        List<Booking> bookings = bookingRepository
                .findByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), now);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.plusDays(3));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.plusDays(1));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDesc_shouldReturnBookings() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking booking2 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));
        Booking booking3 = createBooking(booker, Status.APPROVED, now.plusDays(5), now.plusDays(6));

        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.plusDays(3));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.plusDays(1));
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc_shouldReturnAllBookings() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking booking2 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.plusDays(3));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.plusDays(1));
    }

    @Test
    void findCurrentByOwnerId_shouldReturnCurrentBookings() {
        Booking past = createBooking(booker, Status.WAITING, now.minusDays(3), now.minusDays(2));
        Booking current = createBooking(booker, Status.WAITING, now.minusHours(1), now.plusHours(2));
        Booking future = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> bookings = bookingRepository.findCurrentByOwnerId(owner.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(current.getId());
    }

    @Test
    void findByItemOwnerIdAndEndBeforeOrderByStartDesc_shouldReturnPastBookings() {
        Booking past1 = createBooking(booker, Status.WAITING, now.minusDays(5), now.minusDays(3));
        Booking past2 = createBooking(booker, Status.WAITING, now.minusDays(4), now.minusDays(2));
        Booking future = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));

        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(owner.getId(), now);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.minusDays(4));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.minusDays(5));
    }

    @Test
    void findByItemOwnerIdAndStartAfterOrderByStartDesc_shouldReturnFutureBookings() {
        Booking past = createBooking(booker, Status.WAITING, now.minusDays(3), now.minusDays(2));
        Booking future1 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking future2 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));

        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(owner.getId(), now);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isEqualTo(now.plusDays(3));
        assertThat(bookings.get(1).getStart()).isEqualTo(now.plusDays(1));
    }

    @Test
    void findAllLastBookingsByOwnerId_shouldReturnLastBookings() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.minusDays(5), now.minusDays(4));
        Booking booking2 = createBooking(booker, Status.WAITING, now.minusDays(3), now.minusDays(2));
        Booking booking3 = createBooking(booker, Status.WAITING, now.minusDays(1), now.plusDays(1));

        List<Booking> bookings = bookingRepository.findAllLastBookingsByOwnerId(owner.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
    }

    @Test
    void findLastBookingByOwnerAndItem_shouldReturnLastBooking() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.minusDays(5), now.minusDays(4));
        Booking booking2 = createBooking(booker, Status.WAITING, now.minusDays(3), now.minusDays(2));
        Booking booking3 = createBooking(booker, Status.WAITING, now.minusDays(1), now.plusDays(1));

        Optional<Booking> found = bookingRepository
                .findLastBookingByOwnerAndItem(owner.getId(), item.getId(), now);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(booking2.getId());
    }

    @Test
    void findAllNextBookingsByOwnerId_shouldReturnNextBookings() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.minusDays(5), now.minusDays(4));
        Booking booking2 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking booking3 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));

        List<Booking> bookings = bookingRepository.findAllNextBookingsByOwnerId(owner.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
    }

    @Test
    void findNextBookingByOwnerAndItem_shouldReturnNextBooking() {
        Booking booking1 = createBooking(booker, Status.WAITING, now.minusDays(5), now.minusDays(4));
        Booking booking2 = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        Booking booking3 = createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));

        Optional<Booking> found = bookingRepository
                .findNextBookingByOwnerAndItem(owner.getId(), item.getId(), now);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(booking2.getId());
    }

    @Test
    void existsCompletedBookingByUserAndItem_shouldReturnTrue_whenCompletedBookingExists() {
        Booking booking = createBooking(booker, Status.APPROVED, now.minusDays(3), now.minusDays(2));

        boolean exists = bookingRepository
                .existsCompletedBookingByUserAndItem(booker.getId(), item.getId(), now);

        assertThat(exists).isTrue();
    }

    @Test
    void existsCompletedBookingByUserAndItem_shouldReturnFalse_whenNoCompletedBooking() {
        Booking booking = createBooking(booker, Status.WAITING, now.minusDays(3), now.minusDays(2));

        boolean exists = bookingRepository
                .existsCompletedBookingByUserAndItem(booker.getId(), item.getId(), now);

        assertThat(exists).isFalse();
    }

    @Test
    void count_shouldReturnCorrectCount() {
        createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));
        createBooking(booker, Status.WAITING, now.plusDays(3), now.plusDays(4));

        long count = bookingRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void delete_shouldDeleteBooking() {
        Booking booking = createBooking(booker, Status.WAITING, now.plusDays(1), now.plusDays(2));

        bookingRepository.delete(booking);

        assertThat(bookingRepository.findById(booking.getId())).isEmpty();
    }

    private Booking createBooking(User booker, Status status, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}