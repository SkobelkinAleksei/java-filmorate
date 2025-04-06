package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;

@AllArgsConstructor
@Service
@Data
@Slf4j
public class UserService {
    private final UserDbStorage userDbStorage;

    public List<User> findAll() {
        List<User> allUser = userDbStorage.findAll();
        if (allUser.isEmpty()) {
            return Collections.emptyList();
        }
        return allUser;
    }

    public User getUser(Long userId) {
        if (userDbStorage.getUser(userId) == null) {
            throw new NotFoundException("User  with id %s not found".formatted(userId));
        }

        return userDbStorage.getUser(userId);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userDbStorage.create(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("id не может быть NULL");
        }

        if (userDbStorage.getUser(newUser.getId()) == null) {
            throw new NotFoundException("Пользователь с id %s не был найден ".formatted(newUser.getId()));
        }

        return userDbStorage.update(newUser);
    }

    public List<User> getFriends(Long userId) {
        if (userDbStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с id %s не был найден ".formatted(userId));
        }
        return userDbStorage.getFriends(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        // проверка, что такой юзер есть
        if (userDbStorage.getUser(userId) == null || userDbStorage.getUser(friendId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        userDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        // проверка, что такой юзер есть
        if (userDbStorage.getUser(userId) == null || userDbStorage.getUser(friendId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        userDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getMutualFriends(Long user1, Long user2) {
        // проверка, что такой юзер есть
        if (userDbStorage.getUser(user1) == null || userDbStorage.getUser(user2) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return userDbStorage.getMutualFriends(user1, user2);
    }
}
