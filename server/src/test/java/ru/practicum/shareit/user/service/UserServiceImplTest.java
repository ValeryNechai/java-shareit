package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getUserById_WhenUserFound_ThenReturnedUser() {
        long userId = 1L;
        User expectedUser = User.builder()
                .id(userId)
                .name("UserName")
                .email("qwert@mail.ru")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto expectedUserDto = UserMapper.mapToUserDto(expectedUser);
        UserDto actualUserDto = userService.getUser(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void getUserById_WhenUserNotFound_ThenNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }
}