package ru.yandex.practicum.filmorate.service;

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

    public User getUser(long userId) {
        User user = userDbStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("User  with id %s not found".formatted(userId));
        }
        return user;
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Емаил не может быть пустым");
        }
        return userDbStorage.create(user);
    }

    public User update(User newUser) {
        return userDbStorage.update(newUser);
    }

    public List<User> getFriends(long userId) {
        return userDbStorage.getFriends(userId);
    }

    public boolean addFriend(long userId, long friendId) {
        // проверка, что такой юзер есть
        userDbStorage.isCorrectUser(userId);
        userDbStorage.isCorrectUser(friendId);

        // Проверка, что пользователь не является другом
        if (userDbStorage.isUserExistFriend(userId, friendId)) {
            throw new IllegalArgumentException("User with id = %s already exist friend with id = %s".formatted(userId, friendId));
        }

        // Добавление в друзья
        boolean userAdded = userDbStorage.addFriend(userId, friendId);
        boolean friendAdded = userDbStorage.addFriend(friendId, userId);

        return userAdded && friendAdded; // Возвращаем true, если оба добавлены
    }

    public boolean removeFriend(long userId, long friendId) {
        if (!userDbStorage.isUserExistFriend(userId, friendId)) {
            throw new IllegalArgumentException("User with id = %s has not friend with id = %s".formatted(userId, friendId));
        }
        return userDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getMutualFriends(long user1, long user2) {
        List<User> mutualFriends = userDbStorage.getMutualFriends(user1, user2);
        if (mutualFriends.isEmpty()) {
            return Collections.emptyList();
        }

        return mutualFriends;
    }
}