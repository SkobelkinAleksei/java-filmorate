package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.LogAndThrowHelper;
import ru.yandex.practicum.filmorate.utils.UserValidHelper;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Data
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final LogAndThrowHelper logHelper;
    private final UserValidHelper userHelper;

    @Override
    public Collection<User> findAll() {
        log.info("Получение списка всех Пользователей");
        return users.values();
    }

    @Override
    public User getUser(long userId) {
        log.info("Получение конкретного User по id");
        User user = users.get(userId);

        if (user == null) {
            logHelper.logAndThrow(new NotFoundException(ExceptionMessages.USER_NOT_FOUND));
        }

        return user;
    }

    @Override
    public User create(@Valid User user) {
        log.info("Создание пользователя {}", user);

        duplMailCheck(user);
        userHelper.validateEmail(user.getEmail());
        userHelper.validateLogin(user.getLogin());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userHelper.validateBirthday(user.getBirthday());

        // Тут дали Пользователю уникальный id
        user.setId(getNextId());
        user.setFriendIds(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Создали успешно пользователя {}", user);
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public void duplMailCheck(User user) {
        for (User us : users.values()) {
            if (us.getEmail().equals(user.getEmail())) {
                logHelper.logAndThrow(new DuplicateException(ExceptionMessages.EMAIL_ALREADY_EXISTS));
            }
        }
    }

    @Override
    public User update(@Valid User newUser) {
        log.info("Обновление пользователя с ID: {}", newUser.getId());

        if (newUser.getId() == null) {
            throw new ValidationException("ID не может быть null");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName() != null ? newUser.getName() : newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            return oldUser;
        } else {
            logHelper.logAndThrow(new NotFoundException("Пользователь с id = " + newUser.getId() + " не может быть найден"));
        }
        return null;
    }

    @Override
    public Set<User> getFriends(long userId) {
        log.info("Получаем всех друзей у User по его ID");
        // Получаем пользователя по userId
        User user = getUser(userId);

        // Получаем множество ID друзей и конвертируем их в пользователей
        return user.getFriendIds()
                .stream()
                .map(friendId -> users.get(friendId))
                .filter(Objects::nonNull)  // Фильтруем null значения
                .collect(Collectors.toSet());
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        if (userId == 0 || friendId == 0) {
            logHelper.logAndThrow(new NullPointerException("UserId и FriendId не могут быть равны нулю"));
            return false;
        }

        User user = getUser(userId);
        User friend = getUser(friendId);


        log.info("Добавляем User нового друга");
        boolean addFromUser = user.getFriendIds().add(friendId);


        log.info("Добавляем Новому другу в друзья User");
        boolean addFromFriend = friend.getFriendIds().add(userId);

        if (addFromUser && addFromFriend) {
            log.info("Друзья были успешно добавлены: UserId = {}, FriendId = {}", userId, friendId);
            return true;
        } else {
            log.info("Ошибка при добавлении друзей: UserId = {}, FriendId = {}", userId, friendId);
            return false;
        }
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        if (userId == 0 || friendId == 0) {
            logHelper.logAndThrow(new IllegalArgumentException("UserId и FriendId не могут быть равны нулю"));
            return false;
        }

        User user = getUser(userId);
        User friend = getUser(friendId);

        log.info("Проверяем есть ли взаимность ");
        if (!user.getFriendIds().contains(friendId) || !friend.getFriendIds().contains(userId)) {
            logHelper.logAndThrow(new NullPointerException("Не являются общими друзьями"));
            return false;
        }

        log.info("Удаляем у User друга по ID");
        boolean removedFromUser = user.getFriendIds().remove(friendId);

        log.info("Удаляем у Друга user по ID");
        boolean removedFromFriend = friend.getFriendIds().remove(userId);

        if (removedFromUser && removedFromFriend) {
            log.info("Друзья были успешно удалены: UserId = {}, FriendId = {}", userId, friendId);
            return true;
        } else {
            log.error("Ошибка при удалении друзей: UserId = {}, FriendId = {}", userId, friendId);
            return false;
        }
    }

    @Override
    public List<User> getMutualFriends(Long user1, Long user2) {
        log.info("Получаем пользователей по ID");
        User userOne = users.get(user1);
        User userTwo = users.get(user2);

        log.info("Проверяем, что не null");
        if (userOne == null || userTwo == null) {
            logHelper.logAndThrow(new NotFoundException("Один из пользователей не найден"));
        }

        log.info("Закидываем все id в Set, а дальше преобразуем в список");
        Set<Long> commonFriendsIds = new HashSet<>(userOne.getFriendIds());
        commonFriendsIds.retainAll(userTwo.getFriendIds());

        log.info("Преобразуем в список");
        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : commonFriendsIds) {
            User friend = users.get(friendId); // Получаем пользователя по ID
            if (friend != null) { // Проверяем, существует ли такой пользователь
                commonFriends.add(friend);
            }
        }

        return commonFriends;
    }
}
