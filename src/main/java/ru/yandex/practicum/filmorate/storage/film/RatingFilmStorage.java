package ru.yandex.practicum.filmorate.storage.film;

import java.util.List;

public interface RatingFilmStorage {
    List<Mpa> getAll();

    Mpa getById(Long id);

    Mpa getByName(String name);
}
