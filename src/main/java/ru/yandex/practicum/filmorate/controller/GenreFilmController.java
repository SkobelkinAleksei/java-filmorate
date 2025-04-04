package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreFilmController {
    private final GenreService genreService;

    @GetMapping
    public List<GenreFilm> getAll() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public GenreFilm getById(@PathVariable Long id) {
        return genreService.getById(id);
    }
}
