package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

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

    @Test
    void createUser_WhenEmailAlreadyExists_ThenThrowConflictException() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("Name");
        newUser.setEmail("name@mail.ru");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(newUser));
    }

    @Test
    void createUser_whenUserValid_thenSavedUser() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("Name");
        newUser.setEmail("name@mail.ru");

        User user = User.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUser = userService.createUser(newUser);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("Name", savedUser.getName());
        assertEquals("name@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFound_thenUpdatedAvailableFields() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("UserName")
                .email("qwert@mail.ru")
                .build();

        UpdateUserRequest userToUpdate = new UpdateUserRequest();
        userToUpdate.setName("UserName2");
        userToUpdate.setEmail("qwe5555rt@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User updatedUser = UserMapper.updateUserFields(user, userToUpdate);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        UserDto actualUser = userService.updateUser(userId, userToUpdate);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("UserName2", savedUser.getName());
        assertEquals("qwe5555rt@mail.ru", savedUser.getEmail());
    }

    @Test
    void getAllUsers_WhenUsersExist_ShouldReturnListOfUsers() {
        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .name("User 1")
                        .email("user1@mail.ru")
                        .build(),
                User.builder()
                        .id(2L)
                        .name("User 2")
                        .email("user2@mail.ru")
                        .build()
        );

        when(userRepository.findAll()).thenReturn(users);

        Collection<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(UserDto::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUser_whenUserExists_thanDeleteUser() {
        long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void validateUser_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUser(userId));

        assertEquals("Пользователь с id = 999 не найден.", exception.getMessage());
    }
}