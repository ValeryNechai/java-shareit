package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody NewBookingRequest newBooking,
                                    @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.createBooking(newBooking, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.updateBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsByUser(@RequestParam(required = false) BookingStatus status,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getAllBookingsByUser(status, userId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsByItemByUserId(@RequestParam(required = false) BookingStatus state,
                                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getAllBookingsByItemByUserId(state, userId);
    }
}
