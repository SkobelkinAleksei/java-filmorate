package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    boolean addLike(Long filmId, Long userId);

    boolean removeLike(Long filmId, Long userId);

    Collection<Film> getTopMovies();

    Optional<Film> getFilm(Long filmId);

    int createFilm(Film film) throws ValidationException;

    Optional<Film> update(Film newFilm);
}
