package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.modelFilm.Film;
import ru.yandex.practicum.filmorate.model.modelFilm.FilmCreate;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmDbStorage;

    public List<Film> findAll() {
        List<Film> films = filmDbStorage.findAll();
        log.info("Найдено {} фильмов", films.size());
        return films;
    }

    public boolean addLike(Long filmId, Long userId) {
        log.info("Добавление лайка: фильм с id {} от пользователя с id {}", filmId, userId);
        return filmDbStorage.addLike(filmId, userId);
    }

    public boolean removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка: фильм с id {} от пользователя с id {}", filmId, userId);
        return filmDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopMovies() {
        List<Film> topMovies = filmDbStorage.getTopMovies();
        log.info("Получены топ {} фильмов", topMovies.size());
        return topMovies;
    }

    public Film getFilm(Long filmId) {
        log.info("Запрос информации о фильме с id {}", filmId);
        return filmDbStorage.getFilm(filmId).orElseThrow(
                () -> new IllegalArgumentException("Фильм с id %s не найден".formatted(filmId))
        );
    }

    public int createFilm(FilmCreate film) {
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
        return filmDbStorage.update(newFilm).orElseThrow(
                () -> new IllegalArgumentException("Фильм с id %s не найден".formatted(newFilm.getId()))
        );
    }
}
