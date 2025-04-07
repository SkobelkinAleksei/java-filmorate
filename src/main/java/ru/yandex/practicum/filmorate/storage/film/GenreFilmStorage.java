package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface GenreFilmStorage {
    List<GenreFilm> getAll();

    GenreFilm getById(Long id);

    List<Film> getGenresOfFilm(Long id);
}
