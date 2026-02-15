package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUser) {
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new ConflictException("Данный email уже существует!");
        }
        User user = User.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();

        User createdUser = userRepository.save(user);
        log.debug("Пользователь {} успешно добавлен.", user.getName());

        return UserMapper.mapToUserDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UpdateUserRequest newUser) {
        User user = validateUser(userId);
        if (newUser.getEmail() != null && userRepository.existsByEmail(newUser.getEmail())) {
            throw new ConflictException("Данный email уже существует!");
        }
        User updateUser = UserMapper.updateUserFields(user, newUser);

        User updatedUser = userRepository.save(updateUser);
        log.debug("Данные пользователя с id = {} успешно обновлены.", userId);

        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        User user = validateUser(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.debug("Пользователь с id = {} успешно удален.", userId);
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id = " + userId + " не найден.");
                });
    }
}
