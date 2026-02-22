package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewCommentRequest {
    @NotBlank(message = "Комментарий не может быть пустым или null.")
    private String text;

    private LocalDateTime created = LocalDateTime.now();
}
