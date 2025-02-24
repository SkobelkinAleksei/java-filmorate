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

}
