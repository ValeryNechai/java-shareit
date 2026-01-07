package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@EqualsAndHashCode(of = {"email"})
public class User {
    private Long id;
    private String name;
    private String email;
}
