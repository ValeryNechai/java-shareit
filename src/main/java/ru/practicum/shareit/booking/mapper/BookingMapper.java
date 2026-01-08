package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

public final class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBookerId())
                .itemId(booking.getItemId())
                .startBooking(booking.getStartBooking())
                .endBooking(booking.getEndBooking())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }
}
