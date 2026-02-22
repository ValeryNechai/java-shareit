package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public final class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        UserDto userDto = UserDto.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .email(booking.getBooker().getEmail())
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .description(booking.getItem().getDescription())
                .available(booking.getItem().getAvailable())
                .build();

        return BookingDto.builder()
                .id(booking.getId())
                .booker(userDto)
                .item(itemDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
