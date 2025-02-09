package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;


public interface FilmService {

    Collection<Film> getFilms();

    // Добавляем фильм
    Film createFilm(@Valid Film film) throws ValidationException;

    Film update(@Valid Film newFilm);

    // вспомогательный метод для генерации идентификатора нового поста
    long getNextId();

    void validateFilmId(Long id);

    void validateFilm(Film film) throws ValidationException;

    void logAndThrow(String message) throws ValidationException;

}