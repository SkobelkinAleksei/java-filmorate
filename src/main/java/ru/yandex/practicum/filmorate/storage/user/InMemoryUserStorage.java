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
        user.setFriendIds(new HashMap<>());
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
            oldUser.setFriendIds(newUser.getFriendIds());
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
                .entrySet()  // Получаем все записи (ключ, значение) из Map
                .stream()
                .filter(entry -> entry.getValue() == StatusFriend.FRIEND)  // Отбираем только тех, у кого статус "Friends"
                .map(entry -> users.get(entry.getKey()))  // Получаем User по ключу (ID друга)
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

        log.info("Проверяем, если заявка висит в друзьях");
        if (user.getFriendIds().get(friend.getId()) == StatusFriend.WAITING_FOR_ANSWER) {
            log.info("Заявка в друзья висит");

            user.getFriendIds().put(friend.getId(), StatusFriend.FRIEND);
            friend.getFriendIds().put(user.getId(), StatusFriend.FRIEND);
            log.info("Приняли зявку и сделали взаимными друзьями");
            return true;

        } else if (!user.getFriendIds().containsKey(friend.getId())) {
            log.info("Отправляем заявку, друзья не были добавлены");

            user.getFriendIds().put(friend.getId(), StatusFriend.FRIEND_REQUEST);
            log.info("Добавили friend к User, но со статусом отправленной заявки");

            friend.getFriendIds().put(user.getId(), StatusFriend.WAITING_FOR_ANSWER);
            log.info("Добавили User к friend, но со статусом ожидания принятия");
            return true;
        } else {
            log.error("Заявка либо была отправлена, либо уже являются друзьями");
            return false;
        }
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        if (userId == 0 || friendId == 0) {
            log.error("UserId и FriendId не могут быть равны нулю");
            return false;
        }

        User user = getUser(userId);
        User friend = getUser(friendId);

        log.info("Проверяем есть ли взаимность ");
        if (!(user.getFriendIds().get(friend.getId()) == StatusFriend.FRIEND)) {

            log.error("Не являются общими друзьями");
            return false;
        }

        log.info("Удаляем у User друга по ID");
        user.getFriendIds().remove(friend.getId());
        boolean removedFromUser = user.getFriendIds().containsKey(friend.getId());

        log.info("Удаляем у Друга user по ID");
        friend.getFriendIds().remove(userId);
        boolean removedFromFriend = friend.getFriendIds().containsKey(user.getId());

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
        Set<Long> userOneFriendIds = userOne.getFriendIds().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == StatusFriend.FRIEND) // Статус "Friends"
                .map(Map.Entry::getKey) // Получаем только ID друзей
                .collect(Collectors.toSet());

        // Теперь получаем все ID друзей для userTwo
        Set<Long> userTwoFriendIds = userTwo.getFriendIds().entrySet()
                .stream()
                .filter(entry -> entry.getValue() == StatusFriend.FRIEND) // Статус "Friends"
                .map(Map.Entry::getKey) // Получаем только ID друзей
                .collect(Collectors.toSet());

        // Пересечение ID друзей
        userOneFriendIds.retainAll(userTwoFriendIds);

        log.info("Преобразуем пересеченные ID в список пользователей");
        // Теперь конвертируем пересеченные ID обратно в объекты User
        List<User> commonFriends = userOneFriendIds.stream()
                .map(users::get) // Получаем пользователя по ID
                .filter(Objects::nonNull) // Отфильтровываем null значения
                .collect(Collectors.toList()); // Собираем в список

        return commonFriends;

    }
}
