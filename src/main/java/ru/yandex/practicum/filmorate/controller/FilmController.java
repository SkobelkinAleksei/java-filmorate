package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    // Получение всех фильмов.
    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    // Добавляем фильм
    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Создание фильма: {}", film);

        if (film.getName() == null || film.getName().trim().isEmpty()) {
            log.error("Ошибка валидации: Имя должно быть указано {}", film.getName());
            throw new ValidationException("Имя должно быть указано");
        }

        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("Ошибка валидации: Описание фильма пустое или превышает 200 символов {}", film.getDescription());
            throw new ValidationException("Описание фильма пустое или превышает 200 символов");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка валидации: Дата фильма вышла неверно указана {}", film.getReleaseDate());
            throw new ValidationException("Дата фильма вышла неверно указана");
        }

        if (film.getDuration() < 0) {
            log.error("Ошибка валидации: Продолжительность фильма не может быть меньше 0 {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма не может быть меньше 0");
        }

        // Тут дали фильму определенный id
        film.setId(getNextId());
        // Закинули в Mапу
        films.put(film.getId(), film);
        log.info("Создали фильм: {}", film);
        return film;
    }

    // Обновляем данные фильма
    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.info("Ошибка валидации при обновлении данных о фильме: Не было передано ID {}", newFilm);
            throw new ValidationException("Вы не передали ID");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getName() == null || newFilm.getDescription().trim().isEmpty()) {
                log.error("Ошибка валидации при обновлении данных о фильме: Имя должно быть указано {}", newFilm.getName());
                throw new ValidationException("Имя должно быть указано");
            }

            if (newFilm.getDescription() == null || newFilm.getDescription().length() > 200) {
                log.error("Ошибка валидации при обновлении данных о фильме: Описание фильма пустое или превышает 200 символов {}", newFilm.getDescription());
                throw new ValidationException("Описание фильма пустое или превышает 200 символов");
            }

            if (newFilm.getReleaseDate() == null || newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("Ошибка валидации при обновлении данных о фильме: Дата фильма вышла неверно указана {}", newFilm.getReleaseDate());
                throw new ValidationException("Дата фильма вышла неверно указана");
            }

            if (newFilm.getDuration() < 0) {
                log.error("Ошибка валидации при обновлении данных о фильме: Продолжительность фильма не может быть меньше 0 {}", newFilm.getDuration());
                throw new ValidationException("Продолжительность фильма не может быть меньше 0");
            }

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм успешно был изменен {}", oldFilm);
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не может быть найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
