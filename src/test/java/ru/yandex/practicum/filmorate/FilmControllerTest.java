package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        filmController = new FilmController(new FilmService());

        validFilm = Film.builder()
            .id(0L)
            .name("Кинг-конг")
            .description("Какое-то описание")
            .releaseDate(LocalDate.parse("2012-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .duration(120)
            .build();

        filmController.create(validFilm);

        invalidFilmName = Film.builder()
            .id(0L)
            .name(" ")
            .description("Какое-то описание без имени фильма")
            .releaseDate(LocalDate.parse("2022-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .duration(100)
            .build();

        invalidFilmDescription = Film.builder()
            .id(0L)
            .name("Название фильма")
            .description("a".repeat(201))
            .releaseDate(LocalDate.parse("2020-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .duration(100)
            .build();

        invalidFilmReleaseDate = Film.builder()
            .id(0L)
            .name("Фильм с неверной датой")
            .description("Описание фильма с неверной датой")
            .releaseDate(LocalDate.parse("1880-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .duration(100)
            .build();

        filmWithNoId = Film.builder()
            .id(null)
            .name("Фильм без айди")
            .description("Описание фильма без айди")
            .releaseDate(LocalDate.parse("2020-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .duration(100)
            .build();

        filmWithWrongId = Film.builder()
            .id(11L)
            .name("Фильм с неправильным айди")
            .description("Описание фильма с неправильным айди")
            .releaseDate(LocalDate.parse("2020-02-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .duration(100)
            .build();
    }

    @Test
    public void сreateValidFilmTest() throws ValidationException {
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
        assertNotNull(filmController.getFilms());
    }
}