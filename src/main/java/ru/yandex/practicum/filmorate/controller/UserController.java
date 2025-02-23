package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // Получение всех Пользователей.
    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    // Получение фильма
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUser(id);
    }

    // Добавляем Пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    // Обновляем данные Пользователя
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    // Добавление в друзья
    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
    }

    // Удаление из друзей
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.removeFriend(userId, friendId);
    }

    // Возвращаем друзей User
    @GetMapping("/users/{id}/friends")
    public Set<User> getFriends(@PathVariable long userId) {
        return userService.getFriends(userId);
    }

    // Список общих друзей
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Set<User> getMutualFriends(@PathVariable User user1, @PathVariable User user2) {
        return userService.getMutualFriends(user1, user2);
    }
}
