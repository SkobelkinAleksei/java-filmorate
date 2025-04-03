package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.RatingFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingFilmDbStorage ratingFilm;

    public Collection<RatingFilm> getAll() {
        return ratingFilm.getAll();
    }

    public String getRating(Integer id) {
        return ratingFilm.getRating(id);
    }
}