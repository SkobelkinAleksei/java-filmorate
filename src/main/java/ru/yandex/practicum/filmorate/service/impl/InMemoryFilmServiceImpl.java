package ru.yandex.practicum.filmorate.service.impl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class InMemoryFilmServiceImpl implements FilmService {

    private static final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    // Добавляем фильм
    @Override
    public Film createFilm(@Valid Film film) throws ValidationException {
        log.info("Создание фильма: {}", film);
        validateFilm(film);
        // Тут дали фильму уникальный id
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создали фильм: {}", film);
        return film;
    }

    @Override
    public Film update(@Valid Film newFilm) {
        log.info("Обновление данных о фильме: {}", newFilm);
        validateFilmId(newFilm.getId());

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

    @Override
    public void validateFilmId(Long id) {
        if (id == null) {
            logAndThrow(ExceptionMessages.FILM_ID_CANNOT_BE_NULL);
        }
    }

    @Override
    public void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            logAndThrow(ExceptionMessages.FILM_NAME_CANNOT_BE_EMPTY);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            logAndThrow(ExceptionMessages.FILM_DESCRIPTION_TOO_LONG);
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            logAndThrow(ExceptionMessages.FILM_RELEASE_DATE_IS_INCORRECT);
        }
        if (film.getDuration() <= 0) {
            logAndThrow(ExceptionMessages.FILM_LENGTH_IS_NEGATIVE);
        }
    }

    @Override
    public void logAndThrow(String message) throws ValidationException {
        log.error(message);
        throw new ValidationException(message);
    }

}
