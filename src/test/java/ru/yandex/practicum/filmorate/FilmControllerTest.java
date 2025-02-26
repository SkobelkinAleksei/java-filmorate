package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.utils.FilmValidHelper;
import ru.yandex.practicum.filmorate.utils.LogAndThrowHelper;
import ru.yandex.practicum.filmorate.utils.UserValidHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private static FilmController filmController;
    private static Film validFilm;
    private static Film invalidFilmName;
    private static Film invalidFilmDescription;
    private static Film invalidFilmReleaseDate;
    private static Film filmWithNoId;
    private static Film filmWithWrongId;

    @BeforeAll
    public static void start() throws ValidationException {
        // Создаем экземпляр сервиса
        FilmService filmService = new FilmService(new InMemoryFilmStorage(new FilmValidHelper(new LogAndThrowHelper()), new LogAndThrowHelper(), new UserService(new InMemoryUserStorage(new LogAndThrowHelper(), new UserValidHelper(new LogAndThrowHelper())))));

        // Создаем контроллер, передавая ему сервис и хранилище
        filmController = new FilmController(filmService);

        validFilm = Film.builder()
                .id(0L)
                .name("Кинг-конг")
                .description("Какое-то описание")
                .releaseDate(LocalDate.parse("2012-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(120)
                .userLikes(new HashSet<>())
                .build();

        filmController.create(validFilm);

        invalidFilmName = Film.builder()
                .id(0L)
                .name(" ")
                .description("Какое-то описание без имени фильма")
                .releaseDate(LocalDate.parse("2022-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(100)
                .userLikes(new HashSet<>())
                .build();

        invalidFilmDescription = Film.builder()
                .id(0L)
                .name("Название фильма")
                .description("a".repeat(201))
                .releaseDate(LocalDate.parse("2020-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(100)
                .userLikes(new HashSet<>())
                .build();

        invalidFilmReleaseDate = Film.builder()
                .id(0L)
                .name("Фильм с неверной датой")
                .description("Описание фильма с неверной датой")
                .releaseDate(LocalDate.parse("1880-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(100)
                .userLikes(new HashSet<>())
                .build();

        filmWithNoId = Film.builder()
                .id(null)
                .name("Фильм без айди")
                .description("Описание фильма без айди")
                .releaseDate(LocalDate.parse("2020-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(100)
                .userLikes(new HashSet<>())
                .build();

        filmWithWrongId = Film.builder()
                .id(11L)
                .name("Фильм с неправильным айди")
                .description("Описание фильма с неправильным айди")
                .releaseDate(LocalDate.parse("2020-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .duration(100)
                .userLikes(new HashSet<>())
                .build();
    }

    @Test
    public void createValidFilmTest() throws ValidationException {
        assertEquals(filmController.create(validFilm), validFilm);
    }

    @Test
    public void creatingFilmWithEmptyNameTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(invalidFilmName);
        });
        assertNotNull(exception);
        assertEquals("Название фильма должно быть указано", exception.getMessage());
    }

    @Test
    public void creatingFilmWithTooLongDescriptionTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(invalidFilmDescription);
        });
        assertNotNull(exception);
        assertEquals("Описание фильма пустое или превышает 200 символов", exception.getMessage());
    }

    @Test
    public void oldReleaseDateTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.create(invalidFilmReleaseDate);
        });
        assertNotNull(exception);
        assertEquals("Дата релиза фильма не может быть раньше 28.12.1895г.", exception.getMessage());
    }

    @Test
    public void filmWithNoIdTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.update(filmWithNoId);
        });
        assertNotNull(exception);
    }

    @Test
    public void filmWithWrongIdTest() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmController.update(filmWithWrongId);
        });
        assertNotNull(exception);
    }

    @AfterAll
    public static void shouldReturnAllFilmsTest() {
        assertNotNull(filmController.findAll());
    }
}