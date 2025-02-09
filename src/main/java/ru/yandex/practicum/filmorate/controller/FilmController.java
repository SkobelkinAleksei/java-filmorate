package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    // Получение всех фильмов.
    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    // Добавляем фильм
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    // Обновляем данные фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }
}
