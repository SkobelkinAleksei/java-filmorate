package ru.yandex.practicum.filmorate.utils;

import jakarta.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.modelFilm.Film;

public interface FilmValidMethods {
    void validateFilmId(Long id);

    void validateFilm(Film film) throws ValidationException;
}
