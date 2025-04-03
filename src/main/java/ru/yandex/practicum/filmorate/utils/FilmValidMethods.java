package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;
import jakarta.validation.ValidationException;

public interface FilmValidMethods {
    void validateFilmId(Long id);

    void validateFilm(Film film) throws ValidationException;
}
