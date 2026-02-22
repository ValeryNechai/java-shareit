package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewItemDto {
    @NotBlank(message = "Название вещи не может быть пустым или null.")
    private String name;

    @NotNull(message = "Описание вещи не может быть null.")
    @Size(min = 3, max = 255, message = "Описание должно быть от 3 до 255 символов.")
    private String description;

    @NotNull(message = "Доступность вещи должна быть указана.")
    private Boolean available;

    private Long requestId;
}
