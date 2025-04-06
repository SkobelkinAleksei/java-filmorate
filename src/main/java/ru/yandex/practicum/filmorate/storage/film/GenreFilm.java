package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenreFilm {
    @NotNull
    private Long id;
    @NotNull
    private String name;

    public GenreFilm(Long id) {
        this.id = id;
    }
}
