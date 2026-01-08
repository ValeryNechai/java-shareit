package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;

@Component
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User createUser(NewUserRequest newUser) {
        User user = User.builder()
                .id(++id)
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(Long userId, UpdateUserRequest newUser) {
        User updateUser = UserMapper.updateUserFields(users.get(userId), newUser);
        users.put(userId, updateUser);

        return updateUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isExistUserWithThisEmail(NewUserRequest newUser) {
        Optional<User> alreadyExistUser =
                users.values()
                        .stream()
                        .filter(user -> user.getEmail().equals(newUser.getEmail()))
                        .findFirst();

        return alreadyExistUser.isPresent();
    }

    @Override
    public boolean isExistUserWithThisEmail(String email) {
        Optional<User> alreadyExistUser =
                users.values()
                        .stream()
                        .filter(user -> user.getEmail().equals(email))
                        .findFirst();

        return alreadyExistUser.isPresent();
    }

    @Override
    public Optional<User> isExistUser(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }
}
