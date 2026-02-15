package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(NewBookingRequest newBooking, Long bookerId);

    BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId);

    BookingDto getBooking(Long bookingId, Long userId);

    Collection<BookingDto> getAllBookingsByUser(BookingStatus status, Long bookerId);

    Collection<BookingDto> getAllBookingsByItemByUserId(BookingStatus status, Long userId);
}
