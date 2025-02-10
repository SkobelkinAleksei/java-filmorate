package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmValidMethods {
    void validateFilmId(Long id);

    void validateFilm(Film film) throws ValidationException;
}
