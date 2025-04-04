package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.RatingFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;
import ru.yandex.practicum.filmorate.storage.film.RatingFilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingFilmDbStorage ratingFilmStorage;

    public List<RatingFilm> getAll() {
        return ratingFilmStorage.getAll();
    }

    public RatingFilm getById(long id) {
        if (ratingFilmStorage.getById(id) == null) {
            throw new NotFoundException("Рейтинга с таким id = " + id + " нет");
        } else {
            return ratingFilmStorage.getById(id);
        }
    }
}