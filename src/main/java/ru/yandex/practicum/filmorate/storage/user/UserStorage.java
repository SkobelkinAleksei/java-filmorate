package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User getUser(long userId);

    User create(User user);

    long getNextId();

    void duplMailCheck(User user);

    User update(User newUser);

    Set<User> getFriends(long userId);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    Set<User> getMutualFriends(User user1, User user2);
}
