package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    @NotNull(message = "id не может быть null.")
    private Long id;

    @NotNull(message = "Название вещи не может быть null.")
    private String name;

    @NotNull(message = "Описание вещи не может быть null.")
    @Size(min = 3, max = 255, message = "Описание должно быть от 3 до 255 символов.")
    private String description;

    @NotNull(message = "id владельца не может быть null.")
    private Long ownerId;

    @NotNull(message = "Доступность вещи должна быть указана.")
    private Boolean available;

    private Long requestId;
}
