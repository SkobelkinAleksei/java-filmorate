package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.modelUser.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    Optional<User> getUser(long userId);

    int create(User user);

    User update(User newUser);

    List<User> getFriends(long userId);

    boolean addFriend(long userId, long friendId);

    boolean removeFriend(long userId, long friendId);

    List<User> getMutualFriends(Long user1, Long user2);
}
