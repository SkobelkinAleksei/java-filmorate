package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserFriendAlreadyExist;
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
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userDbStorage.create(user);
    }

    public User update(User newUser) {
        User updatedUser = getUser(newUser.getId());

        if (updatedUser == null) {
            throw new NotFoundException("Пользователь с id = %s не найден!".formatted(newUser.getId())); // Измените на NotFoundException
        }

        return userDbStorage.update(newUser);
    }

    public List<User> getFriends(long userId) {
        List<User> friends = userDbStorage.getFriends(userId);
        log.info("Списко друзей %s, %s".formatted(userId, friends));
        return !friends.isEmpty() ? friends : Collections.emptyList();
    }

    public boolean addFriend(long userId, long friendId) {
        // проверка, что такой юзер есть
        if(userDbStorage.getUser(userId) == null || userDbStorage.getUser(friendId) == null){
            throw new NotFoundException("Пользователь не найден");
        }

        Integer userExistFriend = userDbStorage.isUserExistFriend(userId, friendId);
        if( userExistFriend > 1){
            throw new UserFriendAlreadyExist("Пользователь уже добавлен в друзья!");
        }

        // Добавление в друзья
        boolean friendAdded = userDbStorage.addFriend(userId, friendId);
        log.info("User %s take user  %s to friends, status %s".formatted(userId, friendId, friendAdded));
        boolean userAdded = userDbStorage.addFriend(friendId, userId);
        log.info("Friend %s take user %s to friends %s".formatted(friendId, userId, userAdded));

        return friendAdded && userAdded;
    }

    public Boolean removeFriend(long userId, long friendId) {
        Integer userExistFriend = userDbStorage.isUserExistFriend(userId, friendId);
        Integer userExistFriend1 = userDbStorage.isUserExistFriend(friendId, userId);

        List<User> friendsList1 = userDbStorage.getFriends(userId);
        List<User> friendsList2 = userDbStorage.getFriends(friendId);

        if((userExistFriend == 0 && userExistFriend1 == 0) || (friendsList1.isEmpty() || friendsList2.isEmpty())){
            throw new NotFoundException("Пользователи не являются друщьями!");
        }

        boolean removeFirst = userDbStorage.removeFriend(userId, friendId);
        boolean removeSecond = userDbStorage.removeFriend(friendId, userId);

        return null;
    }

    public List<User> getMutualFriends(long user1, long user2) {
        List<User> mutualFriends = userDbStorage.getMutualFriends(user1, user2);
        if (mutualFriends.isEmpty()) {
            return Collections.emptyList();
        }

        return mutualFriends;
    }
}