package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class FilmService {
    private FilmStorage filmStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public boolean addLike(Long filmId, Long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public boolean removeLike(Long filmId, Long userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getTopMovies() {
        return filmStorage.getTopMovies();
    }

    public Film getFilm(Long filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }
}
