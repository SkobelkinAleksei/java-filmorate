package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> findAll();

    User getUser(long userId);

    User create(User user);

    User update(User newUser);

    List<User> getFriends(long userId);

    boolean addFriend(long userId, long friendId);

    boolean removeFriend(long userId, long friendId);

    List<User> getMutualFriends(long user1, long user2);
}
