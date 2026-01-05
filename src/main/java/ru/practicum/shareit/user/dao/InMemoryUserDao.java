package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private static long id = 0;

    @Override
    public UserDto createUser(NewUserRequest newUser) {
        Optional<User> alreadyExistUser =
                users.values()
                        .stream()
                        .filter(user -> user.getEmail().equals(newUser.getEmail()))
                        .findFirst();
        if (alreadyExistUser.isPresent()) {
            throw new ConflictException("Данный email уже существует!");
        }

        User user = User.builder()
                .id(++id)
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();
        users.put(user.getId(), user);
        log.debug("Пользователь {} успешно добавлен.", user.getName());
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest newUser) {
        try {
            User user = users.get(userId);
            Optional<User> alreadyExistUserWithThisEmail =
                    users.values()
                            .stream()
                            .filter(u -> !u.getId().equals(userId))
                            .filter(u -> u.getEmail().equals(newUser.getEmail()))
                            .findFirst();
            if (alreadyExistUserWithThisEmail.isPresent()) {
                throw new ConflictException("Данный email уже существует!");
            }

            User updateUser = UserMapper.updateUserFields(user, newUser);
            users.put(userId, updateUser);
            log.debug("Данные пользователя {} успешно обновлены.", updateUser.getName());

            return UserMapper.mapToUserDto(updateUser);
        } catch (NotFoundException exception) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
        log.debug("Пользователь с id = {} успешно удален.", userId);
    }
}
