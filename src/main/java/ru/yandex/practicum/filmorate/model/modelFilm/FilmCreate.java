package ru.yandex.practicum.filmorate.model.modelFilm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FilmCreate {
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Дата не может быть в будущем")
    private LocalDate releaseDate;
    private int duration;
    private int genreId;
    private int ratingId;

    public FilmCreate(@JsonProperty("name") String name,
                      @JsonProperty("description") String description,
                      @JsonProperty("releaseDate") LocalDate releaseDate,
                      @JsonProperty("duration") int duration,
                      @JsonProperty("genreId") int genreId,
                      @JsonProperty("ratingId") int ratingId) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genreId = genreId == 0 ? 1 : genreId;
        this.ratingId = ratingId == 0 ? 1 : ratingId;
    }
}
