package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    @NotNull(message = "id не может быть null.")
    private Long id;

    @NotNull(message = "Имя пользователя не может быть пустым!")
    private String name;

    @NotNull(message = "Email не может быть пустым!")
    @Email(message = "Некорректный email!")
    private String email;
}
