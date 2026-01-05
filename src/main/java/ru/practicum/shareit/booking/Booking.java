package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class Booking {
    @NotNull(message = "id не может быть null.")
    private Long id;

    @NotNull(message = "id пользователя не может быть null.")
    private Long bookerId;

    @NotNull(message = "id вещи не может быть null.")
    private Long itemId;

    @NotNull(message = "Дата и время начала бронирования должны быть заполнены.")
    @FutureOrPresent(message = "Нельзя назначить дату начала бронирования из прошлого.")
    private LocalDateTime startBooking;

    @NotNull(message = "Дата и время окончания бронирования должны быть заполнены.")
    @Future(message = "Нельзя назначить дату окончания бронирования из прошлого.")
    private LocalDateTime endBooking;

    @NotNull(message = "Статус должен быть определен!")
    private BookingStatus bookingStatus;

    private String review;
}
