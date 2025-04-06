package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;
import ru.yandex.practicum.filmorate.storage.film.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть > 200 символов")
    private String description;

    @NotNull(message = "Дата релиза фильма не может быть пуста или null")
    @Past
    private LocalDate releaseDate;


    @AssertTrue(message = "Дата должна быть после 28 декабря 1895")
    public boolean isValidDate() {
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        return releaseDate != null && releaseDate.isAfter(minDate);
    }

    @NotNull(message = "Продолжительность фильма не может быть пуста или null")
    @Min(1)
    private Integer duration;

    private List<Long> userLikes;
    private Set<GenreFilm> genres;
    private Mpa mpa;
}
