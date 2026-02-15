package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewItemRequestDto {
    @NotNull(message = "Описание вещи не может быть null.")
    private String description;

    private LocalDateTime created = LocalDateTime.now();
}
