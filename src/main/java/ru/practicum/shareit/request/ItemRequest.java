package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "id не может быть null.")
    private Long id;

    @NotNull(message = "Описание запрашиваемой вещи не может быть null.")
    @Size(min = 3, max = 255, message = "Описание должно быть от 3 до 255 символов.")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    @NotNull(message = "Создатель запроса должен быть указан.")
    private User requester;

    @Column(name = "created_date")
    private LocalDateTime created = LocalDateTime.now();
}
