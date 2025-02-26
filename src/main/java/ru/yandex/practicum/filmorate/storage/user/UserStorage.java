package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User getUser(long userId);

    User create(User user);

    long getNextId();

    void duplMailCheck(User user);

    User update(User newUser);

    Set<User> getFriends(long userId);

    boolean addFriend(long userId, long friendId);

    boolean removeFriend(long userId, long friendId);

    List<User> getMutualFriends(Long user1, Long user2);
}
