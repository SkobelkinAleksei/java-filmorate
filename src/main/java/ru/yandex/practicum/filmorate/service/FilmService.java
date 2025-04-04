package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    public List<Film> findAll() {
        List<Film> films = filmDbStorage.findAll();
        log.info("Найдено {} фильмов", films.size());
        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        return films;
    }

    public boolean addLike(Long filmId, Long userId) {
        log.info("Добавление лайка: фильм с id {} от пользователя с id {}", filmId, userId);

        User user = userDbStorage.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with id %s not found!".formatted(userId));
        }

        return filmDbStorage.addLike(filmId, user.getId());
    }

    public boolean removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка: фильм с id {} от пользователя с id {}", filmId, userId);

        User user = userDbStorage.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with id %s not found!".formatted(userId));
        }

        Film film = filmDbStorage.getFilm(filmId).orElseThrow(
                () -> new NotFoundException("Фильм не найден! %s".formatted(filmId))
        );

        return filmDbStorage.removeLike(film.getId(), user.getId());
    }

    public List<Film> getTopMovies() {
        List<Film> topMovies = filmDbStorage.getTopMovies();
        log.info("Получены топ {} фильмов", topMovies.size());
        if (topMovies.isEmpty()) {
            return Collections.emptyList();
        }

        return topMovies;
    }

    public Film getFilm(long filmId) {
        log.info("Запрос информации о фильме с id {}", filmId);

        return filmDbStorage.getFilm(filmId).orElseThrow(
                () -> new NotFoundException("Фильм не найден! %s". formatted(filmId))
        );
    }

    public Film createFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new IllegalArgumentException("Название фильма не может быть пустым");
        }
        if (film.getDuration() <= 0) {
            throw new IllegalArgumentException("Продолжительность фильма должна быть положительной");
        }
        log.info("Создание фильма: {}", film);
        return filmDbStorage.createFilm(film);
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new IllegalArgumentException("ID фильма не может быть null при обновлении");
        }

        log.info("Обновление фильма: {}", newFilm);

        Film updatedFilm = filmDbStorage.update(newFilm); // Предполагаем, что этот метод теперь возвращает Film или null
        if (updatedFilm == null) {
            throw new IllegalArgumentException("Фильм с id %s не найден".formatted(newFilm.getId()));
        }

        return updatedFilm;
    }
}
