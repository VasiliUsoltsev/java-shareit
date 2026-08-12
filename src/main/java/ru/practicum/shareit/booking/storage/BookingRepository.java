package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    public List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    public List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    public List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId,
                                               @Param("now") LocalDateTime now);

    public List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    public List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);


    public List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    public List<Booking> findByItemOwnerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now);

    public List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    public List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now " +
            "AND b.end = (SELECT MAX(b2.end) FROM Booking b2 WHERE b2.item.id = b.item.id AND b2.end < :now)")
    public List<Booking> findAllLastBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.item.id = :itemId " +
            "AND b.end < :now " +
            "AND b.end = (SELECT MAX(b2.end) FROM Booking b2 " +
            "              WHERE b2.item.id = b.item.id " +
            "              AND b2.end < :now)")
    public Optional<Booking> findLastBookingByOwnerAndItem(
            @Param("ownerId") Long ownerId,
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now " +
            "AND b.start = (SELECT MIN(b2.start) FROM Booking b2 WHERE b2.item.id = b.item.id AND b2.start > :now)")
    public List<Booking> findAllNextBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.item.id = :itemId " +
            "AND b.start > :now " +
            "AND b.start = (SELECT MIN(b2.start) FROM Booking b2 " +
            "              WHERE b2.item.id = b.item.id " +
            "              AND b2.start > :now)")
    Optional<Booking> findNextBookingByOwnerAndItem(
            @Param("ownerId") Long ownerId,
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.end < :now " +
            "AND b.status = 'APPROVED'")
    public boolean existsCompletedBookingByUserAndItem(
            @Param("userId") Long userId,
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now
    );
}
