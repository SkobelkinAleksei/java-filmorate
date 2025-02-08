package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    // Получение всех Пользователей.
    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение списка всех Пользователей");
        return users.values();
    }

    // Добавляем Пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание пользователя {}", user);

        if (user.getName() == null || user.getName().isEmpty()) {
            // имя для отображения может быть пустым — в таком случае будет использован логин;
            user.setName(user.getLogin());
        }

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: почта не может быть пустой и должна содержать @ {}", user);
            throw new ValidationException("Почта не может быть пустой и должна содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: Логин не может быть пуст и не должен содержать пробелы {}", user);
            throw new ValidationException("Логин не может быть пуст и не должен содержать пробелы");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: Дата рождения не может быть пустой или дата указана неправильно {}", user);
            throw new ValidationException("Дата рождения не может быть пустой или дата указана неправильно");
        }

        // Тут дали Пользователю определенный id
        user.setId(getNextId());
        // Закинули в Mапу
        users.put(user.getId(), user);
        log.info("Создали успешно пользователя {}", user);

        return user;
    }

    // Обновляем данные Пользователя
    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Обновление пользователя с ID: {}", newUser.getId());

        if (newUser.getId() == null) {
            log.error("Ошибка валидации: ID некорректный {}", newUser.getId());
            throw new ValidationException("Вы не передали ID");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.getName() == null || newUser.getName().isEmpty()) {
                // имя для отображения может быть пустым — в таком случае будет использован логин;
                newUser.setName(newUser.getLogin());
            }

            if (newUser.getEmail() == null || newUser.getEmail().isEmpty() || !newUser.getEmail().contains("@")) {
                log.error("Ошибка валидации при обновлении данных о Пользователе: EMAIL некорректный {}", newUser.getEmail());
                throw new ValidationException("Почта не может быть пустой и должна содержать @ ");
            }

            if (newUser.getLogin() == null || newUser.getLogin().isEmpty() || newUser.getLogin().contains(" ")) {
                log.error("Ошибка валидации при обновлении данных о Пользователе: LOGIN некорректный {}", newUser.getLogin());
                throw new ValidationException("Логин не может быть пуст и не должен содержать пробелы");
            }

            // Проверка на уникальность логина
            for (User user : users.values()) {
                if (!user.getId().equals(newUser .getId()) && user.getLogin().equals(newUser .getLogin())) {
                    log.error("Ошибка валидации при обновлении данных о Пользователе: Логин должен быть уникальным {}", newUser.getEmail());
                    throw new ValidationException("Логин должен быть уникальным");
                }
            }

            if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
                log.error("Ошибка валидации при обновлении данных о Пользователе: Birthday некорректный {}", newUser.getBirthday());
                throw new ValidationException("Дата рождения не может быть пустой или дата указана неправильно");
            }



            oldUser.setName(newUser.getName());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setLogin(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Успешно изменили пользователя {}", oldUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не может быть найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
