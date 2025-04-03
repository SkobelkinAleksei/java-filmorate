package ru.yandex.practicum.filmorate.model.modelFilm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть > 200 символов")
    private String description;

    @NotNull(message = "Дата релиза фильма не может быть пуста или null")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть пуста или null")
    private int duration;

    private Set<Long> userLikes;

    private GenreFilm genre;

    private RatingFilm rating;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film() {
    }

    public Film(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Film(Long id, String name, String description,
                LocalDate releaseDate, int duration, Set<Long> userLikes, GenreFilm genre, RatingFilm rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.userLikes = userLikes;
        this.genre = genre;
        this.rating = rating;
    }

    public Film(Long id, String name, String description,
                LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
