package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import java.util.List;

public interface RatingFilmStorage {

    List<RatingFilm> getAll();

    RatingFilm getById(long id);

    RatingFilm getRatingOfFilm(Long id);
}
