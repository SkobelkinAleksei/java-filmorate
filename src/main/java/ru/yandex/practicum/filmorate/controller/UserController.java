package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // Получение всех Пользователей.
    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    // Получение User
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) throws NotFoundException {
        return userService.getUser(id);
    }

    // Добавляем Пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    // Обновляем данные Пользователя
    @PutMapping
    public User update(@Valid @RequestBody User newUser) throws NotFoundException {
        return userService.update(newUser);
    }

    // Добавление в друзья
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable long friendId) throws NotFoundException {
        userService.addFriend(userId, friendId);
    }

    // Удаление из друзей
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable Long userId, @PathVariable long friendId) {
        userService.removeFriend(userId, friendId);
    }

    // Возвращаем друзей User
    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable Long userId) throws NotFoundException {
        return userService.getFriends(userId);
    }

    // Список общих друзей
    @GetMapping("/{user1}/friends/common/{user2}")
    public List<User> getMutualFriends(@PathVariable Long user1, @PathVariable Long user2) throws NotFoundException {
        return userService.getMutualFriends(user1, user2);
    }
}