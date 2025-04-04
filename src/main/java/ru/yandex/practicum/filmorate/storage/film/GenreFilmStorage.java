package ru.yandex.practicum.filmorate.storage.film;

import java.util.List;

public interface GenreFilmStorage {
    List<GenreFilm> getAll();

    GenreFilm getById(long id);

    List<GenreFilm> getGenresOfFilm(long id);
}
