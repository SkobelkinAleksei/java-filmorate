package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Collection<Film> getFilms();

    // Добавляем фильм
    Film createFilm(Film film) throws ValidationException;

    Film update(Film newFilm);

    // вспомогательный метод для генерации идентификатора нового поста
    long getNextId();
}