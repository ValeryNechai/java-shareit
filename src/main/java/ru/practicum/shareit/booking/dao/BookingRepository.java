package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select count(b)>0 " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 and " +
            "b.status = 'APPROVED' and " +
            "b.start<?3 and b.end>?2")
    boolean existsApprovedBookingInPeriod(Long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findAllBookingsByItemByUserIdAndStatus(Long userId, BookingStatus status);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllBookingsByItemByUserId(Long userId);

    List<Booking> findByItemId(Long itemId);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.item.id = ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.end desc")
    List<Booking> findByBookerIdAndItemIdAndStatusApproved(Long bookerId, Long itemId);
}
