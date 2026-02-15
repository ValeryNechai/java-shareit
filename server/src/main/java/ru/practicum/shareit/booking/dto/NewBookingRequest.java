package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequest {
    @NotNull(message = "id вещи не может быть null.")
    private Long itemId;

    @NotNull(message = "Дата и время начала бронирования должны быть заполнены.")
    @FutureOrPresent(message = "Нельзя назначить дату начала бронирования из прошлого.")
    private LocalDateTime start;

    @NotNull(message = "Дата и время окончания бронирования должны быть заполнены.")
    @Future(message = "Нельзя назначить дату окончания бронирования из прошлого.")
    private LocalDateTime end;

    private BookingStatus status;
}
