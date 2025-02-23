package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
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

    private Set<Long> userLikes = new HashSet<>();
}
