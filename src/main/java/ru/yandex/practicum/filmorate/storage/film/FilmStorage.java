package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getTopMovies();

    Film getFilm(Long filmId);

    Film createFilm(Film film) throws ValidationException;

    Film update(Film newFilm);

    long getNextId();
}
