package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> findAll();

    User create(User user);

    // вспомогательный метод для генерации идентификатора нового поста
    long getNextId();

    void duplMailCheck(User user);

    User update(User newUser);
}