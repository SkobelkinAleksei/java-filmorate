package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.modelUser.User;
import ru.yandex.practicum.filmorate.model.modelUser.UserCreate;
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
        return userDbStorage.getUser(userId).orElseThrow(
                () -> new IllegalStateException("User with id %s not found".formatted(userId))
        );
    }

    public int create(UserCreate user) {
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

    public boolean addFriend(Long userId, Long friendId) {
        userDbStorage.isCorrectUser(friendId);
        if (userDbStorage.isUserExistFriend(userId, friendId)) {
            throw new IllegalArgumentException("User with id = %s already exist friend with id = %s".formatted(userId, friendId));
        }
        return userDbStorage.addFriend(userId, friendId);
    }

    public boolean removeFriend(long userId, long friendId) {
        if (!userDbStorage.isUserExistFriend(userId, friendId)) {
            throw new IllegalArgumentException("User with id = %s has not friend with id = %s".formatted(userId, friendId));
        }
        return userDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getMutualFriends(Long user1, Long user2) {
        List<User> mutualFriends = userDbStorage.getMutualFriends(user1, user2);
        if (mutualFriends.isEmpty()) {
            return Collections.emptyList();
        }

        return mutualFriends;
    }
}