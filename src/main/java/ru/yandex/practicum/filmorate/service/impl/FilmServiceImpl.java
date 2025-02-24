package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.utils.FilmValidHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private static final Map<Long, Film> films = new HashMap<>();
    private final FilmValidHelper filmHelper;

    @Override
    public Collection<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    // Добавляем фильм
    @Override
    public Film createFilm(@Valid Film film) throws ValidationException {
        log.info("Создание фильма: {}", film);
        filmHelper.validateFilm(film);
        // Тут дали фильму уникальный id
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создали фильм: {}", film);
        return film;
    }

    @Override
    public Film update(@Valid Film newFilm) {
        log.info("Обновление данных о фильме: {}", newFilm);
        filmHelper.validateFilmId(newFilm.getId());

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            throw new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND, newFilm.getId()));
        }

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        return oldFilm;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
