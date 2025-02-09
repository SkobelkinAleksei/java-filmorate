package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class InMemoryUserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("Получение списка всех Пользователей");
        return users.values();
    }
    @Override
    public User create(@Valid User user) {
        log.info("Создание пользователя {}", user);

        duplMailCheck(user);
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validateBirthday(user.getBirthday());

        // Тут дали Пользователю уникальный id
        user.setId(getNextId());
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
                logAndThrow(new DuplicateException(ExceptionMessages.EMAIL_ALREADY_EXISTS));
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
            logAndThrow(new NotFoundException("Пользователь с id = " + newUser.getId() + " не может быть найден"));
        }
        return null;
    }

    @Override
    public void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@") || email.contains(" ")) {
            logAndThrow(new ValidationException(ExceptionMessages.EMAIL_CANNOT_BE_EMPTY));
        }
    }

    @Override
    public void validateLogin(String login) {
        if (login == null || login.contains(" ") || login.isEmpty()) {
            logAndThrow(new ValidationException(ExceptionMessages.LOGIN_CANNOT_BE_EMPTY));
        }
    }

    @Override
    public void validateBirthday(LocalDate birthday) {
        if (birthday != null) {
            if (birthday.isAfter(LocalDate.now())) {
                logAndThrow(new ValidationException(ExceptionMessages.BIRTHDAY_CANNOT_BE_IN_FUTURE));
            }
        } else {
            logAndThrow(new ValidationException(ExceptionMessages.BIRTHDAY_CANNOT_BE_NULL));
        }
    }

    @Override
    public void logAndThrow(RuntimeException exception) {
        log.error(exception.getMessage());
        throw exception;
    }
}
