package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Описание запрашиваемой вещи не может быть null.")
    @Size(min = 3, max = 255, message = "Описание должно быть от 3 до 255 символов.")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    @NotNull(message = "Создатель запроса должен быть указан.")
    private User requester;

    @Column(name = "created_date")
    private LocalDateTime created;
}
