package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    // Получение фильма
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) throws NotFoundException {
        return filmService.getFilm(id);
    }

    // Добавляем фильм
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    // Обновляем данные фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) throws NotFoundException {
        return filmService.update(newFilm);
    }

    // Ставим лайк фильму
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) throws NotFoundException {
        filmService.addLike(filmId, userId);
    }

    //  пользователь удаляет лайк
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) throws NotFoundException {
        filmService.removeLike(filmId, userId);
    }

    // Возвращает список из первых 10-и
    @GetMapping("/popular")
    public Collection<Film> getTopMovies() throws NotFoundException {
        return filmService.getTopMovies();
    }
}
