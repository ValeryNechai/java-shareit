package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    User createUser(NewUserRequest user);

    User updateUser(Long userId, UpdateUserRequest newUser);

    Collection<User> getAllUsers();

    Optional<User> getUser(Long id);

    void deleteUser(Long userId);

    boolean isExistUserWithThisEmail(NewUserRequest newUser);

    boolean isExistUserWithThisEmail(String email);

    Optional<User> isExistUser(Long userId);
}
