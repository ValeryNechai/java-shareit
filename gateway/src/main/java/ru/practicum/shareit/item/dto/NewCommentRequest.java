package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentRequest {
    @NotBlank(message = "Комментарий не может быть пустым или null.")
    private String text;

    private LocalDateTime created = LocalDateTime.now();
}
