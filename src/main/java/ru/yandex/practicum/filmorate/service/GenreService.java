package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.GenreFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
@Data
public class GenreService {
    private final GenreFilmDbStorage genreFilmStorage;

    public Collection<GenreFilm> getAll() {
        return genreFilmStorage.getAll();
    }

    public String getGenre(Integer id) {
        return genreFilmStorage.getGenre(id);
    }

    public List<GenreFilm> getGenresOfFilm(Long filmId) {
        return genreFilmStorage.getGenresOfFilm(filmId);
    }

}