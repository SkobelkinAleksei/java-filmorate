package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingFilmController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<RatingFilm> getAll() {
        log.debug("Запрашиваем список возрастных ограничений");
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public String getRating(@PathVariable Integer id) {
        log.debug("Запрос названия рейтинга с ID = {}", id);
        return ratingService.getRating(id);
    }
}
