package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import java.util.List;

public interface GenreFilmStorage {
    Collection<GenreFilm> getAll();

    String getGenre(Integer id);

    List<GenreFilm> getGenresOfFilm(Long filmId);
}
