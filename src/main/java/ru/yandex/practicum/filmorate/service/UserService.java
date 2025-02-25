package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@AllArgsConstructor
@Service
@Data
@Slf4j
public class UserService {
    private InMemoryUserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    public User create(@Valid User user) {
        return userStorage.create(user);
    }

    public User update(@Valid User newUser) {
        return userStorage.update(newUser);
    }

    public Set<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    public boolean addFriend(long userId, long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public boolean removeFriend(long userId, long friendId) {
        return userStorage.removeFriend(userId, friendId);
    }

    public List<User> getMutualFriends(Long user1, Long user2) {
        return userStorage.getMutualFriends(user1, user2);
    }
}
