package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "Название вещи не может быть пустым или null.")
    private String name;

    @NotNull(message = "Описание вещи не может быть null.")
    @Size(min = 3, max = 255, message = "Описание должно быть от 3 до 255 символов.")
    private String description;

    @NotNull(message = "Доступность вещи должна быть указана.")
    private Boolean available;
}
