package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmValidHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Long, Film> films = new HashMap<>();
    private final FilmValidHelper filmHelper;

    @Override
    public Collection<Film> findAll() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Добавляем лайк фильму с ID: {}", filmId);
        Film film = getFilm(filmId);

        if (film == null) {
            log.error("Фильм не найден: {}", filmId);
        }

        if (film.getUserLikes() == null) {
            log.error("userLikes у фильма {} равен null", filmId);
        }

        film.getUserLikes().add(userId);
        log.info("Добавили лайк фильму {}", filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.info("Удаляем лайк у фильма");
        Film film = getFilm(filmId);
        film.getUserLikes().remove(userId);
        log.info("Пользователь с ID = {} забрал лайк с фильма с ID = {}", userId, filmId);
    }

    @Override
    public Collection<Film> getTopMovies() {
        log.info("Собираем фильмы ТОП-10");
        return films.values()
                .stream()
                .sorted((film1, film2) -> Integer.compare(film2.getUserLikes().size(), film1.getUserLikes().size()))
                .limit(10)
                .toList();
    }

    @Override
    public Film getFilm(Long filmId) {
        log.info("Берем фильм по ID {}", filmId);
        Film film = films.get(filmId);

        log.info("Фильм {}", film);
        if (film == null) {
            throw new NotFoundException(ExceptionMessages.FILM_NOT_FOUND);
        }

        return film;
    }

    // Добавляем фильм
    @Override
    public Film createFilm(@Valid Film film) throws ValidationException {
        log.info("Создание фильма: {}", film);
        filmHelper.validateFilm(film);
        // Тут дали фильму уникальный id
        film.setId(getNextId());
        film.setUserLikes(new HashSet<>());
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
