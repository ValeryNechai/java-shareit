package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.*;

public final class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemWithCommentsDto mapToItemWithCommentsDto(Item item, List<Booking> bookingsItems,
                                                     List<Comment> commentsItems) {
        List<Booking> bookingsByOwnerItems = bookingsItems
                .stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .toList();

        List<CommentDto> commentsByOwnerItems = commentsItems
                .stream()
                .filter(comment -> comment.getItem().getId().equals(item.getId()))
                .map(CommentMapper::mapToCommentDto)
                .toList();

        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = findLastBooking(bookingsByOwnerItems, now);
        Booking nextBooking = findNextBooking(bookingsByOwnerItems, now);

        return ItemWithCommentsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentsByOwnerItems)
                .lastBooking(lastBooking != null ? BookingMapper.mapToBookingDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? BookingMapper.mapToBookingDto(nextBooking) : null)
                .build();
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }

    private static Booking findLastBooking(List<Booking> itemBookings, LocalDateTime now) {
        return itemBookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now.minusMinutes(1)))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    private static Booking findNextBooking(List<Booking> itemBookings, LocalDateTime now) {
        return itemBookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }
}
