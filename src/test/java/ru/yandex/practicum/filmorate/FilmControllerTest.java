package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void FilmNameNullTest() {
        Film film = Film.builder()
                .name(null)
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Имя должно быть указано", exception.getMessage());
    }

    @Test
    void FilmDescrTest() {
        Film film = Film.builder()
                .name("Film Name")
                .description("D".repeat(201)) // 201 символ
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Описание фильма пустое или превышает 200 символов", exception.getMessage());
    }

    @Test
    void FilmDateTest() {
        Film film = Film.builder()
                .name("Film Name")
                .description("Description")
                .releaseDate(LocalDate.of(1800, 1, 1)) // до 1895
                .duration(120)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Дата фильма вышла неверно указана", exception.getMessage());
    }

    @Test
    void FilmDurationTest() {
        Film film = Film.builder()
                .name("Film Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(-1) // отрицательная продолжительность
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Продолжительность фильма не может быть меньше 0", exception.getMessage());
    }
}
