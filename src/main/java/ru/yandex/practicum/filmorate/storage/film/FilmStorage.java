package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    boolean addLike(long filmId, long userId);

    boolean removeLike(long filmId, long userId);

    Collection<Film> getTopMovies();

    Film getFilm(long filmId);

    Film createFilm(Film film) throws ValidationException;

    Film update(Film newFilm);
}
