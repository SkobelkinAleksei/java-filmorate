package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;

public interface RatingFilmStorage {

    Collection<RatingFilm> getAll();

    String getRating(Integer id);
}
