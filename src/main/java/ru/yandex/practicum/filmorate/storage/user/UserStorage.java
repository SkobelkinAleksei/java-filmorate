package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> findAll();

    User getUser(Long userId);

    User create(User user);

    User update(User newUser);

    List<User> getFriends(Long userId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getMutualFriends(Long user1, Long user2);
}
