package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto createUser(NewUserRequest newUser) {
        if (userDao.isExistUserWithThisEmail(newUser)) {
            throw new ConflictException("Данный email уже существует!");
        }
        User user = userDao.createUser(newUser);
        log.debug("Пользователь {} успешно добавлен.", user.getName());

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest newUser) {
        validateUser(userId);
        if (newUser.getEmail() != null && userDao.isExistUserWithThisEmail(newUser.getEmail())) {
            throw new ConflictException("Данный email уже существует!");
        }
        User updateUser = userDao.updateUser(userId, newUser);
        log.debug("Данные пользователя с id = {} успешно обновлены.", userId);

        return UserMapper.mapToUserDto(updateUser);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userDao.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        User user = validateUser(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.deleteUser(userId);
        log.debug("Пользователь с id = {} успешно удален.", userId);
    }

    private User validateUser(Long userId) {
        return userDao.getUser(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id = " + userId + " не найден.");
                });
    }
}
