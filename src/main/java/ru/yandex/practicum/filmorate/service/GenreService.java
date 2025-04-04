package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.GenreFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;
import ru.yandex.practicum.filmorate.storage.film.GenreFilmStorage;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
@Data
public class GenreService {
    private final GenreFilmDbStorage genreFilmStorage;

    public List<GenreFilm> getAll() {
        return genreFilmStorage.getAll();
    }

    public GenreFilm getById(Long id) {
        if (genreFilmStorage.getById(id) == null) {
            throw new NotFoundException("Жанра с таким id = " + id + " нет");
        } else {
            return genreFilmStorage.getById(id);
        }
    }
}