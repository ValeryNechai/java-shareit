package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    @NotNull(message = "id не может быть null.")
    private Long id;

    @NotNull(message = "Описание запрашиваемой вещи не может быть null.")
    @Size(min = 3, max = 255, message = "Описание должно быть от 3 до 255 символов.")
    private String description;

    @NotNull(message = "Создатель запроса должен быть указан.")
    private Long requesterId;

    private LocalDateTime created;
}
