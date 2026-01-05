package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserDao {
    UserDto createUser(NewUserRequest user);

    UserDto updateUser(Long userId, UpdateUserRequest newUser);

    Collection<UserDto> getAllUsers();

    UserDto getUser(Long id);

    void deleteUser(Long userId);
}
