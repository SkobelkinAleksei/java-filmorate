package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

public interface UserService {

    Collection<User> findAll();

    User create(@Valid User user);

    // вспомогательный метод для генерации идентификатора нового поста
    long getNextId();

    void duplMailCheck(User user);

    User update(@Valid User newUser);

    void validateEmail(String email);

    void validateLogin(String login);

    void validateBirthday(LocalDate birthday);

    void logAndThrow(RuntimeException exception);
}