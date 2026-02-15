package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewItemRequestDto {
    @NotNull(message = "Описание вещи не может быть null.")
    private String description;

    private LocalDateTime created = LocalDateTime.now();
}
