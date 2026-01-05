package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {
    private Long id;
    private Long bookerId;
    private Long itemId;
    private LocalDateTime startBooking;
    private LocalDateTime endBooking;
    private BookingStatus bookingStatus;
}
