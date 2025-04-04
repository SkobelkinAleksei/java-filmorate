package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingFilmController {
    private final RatingService ratingService;

    @GetMapping
    public List<RatingFilm> getAll() {
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public RatingFilm getById(@PathVariable long id) {
        return ratingService.getById(id);
    }
}
