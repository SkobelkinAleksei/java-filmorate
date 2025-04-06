package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class GenreFilmDbStorage implements GenreFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_GENRE = """
            SELECT g.genre_id, g.genre_type
            FROM genre g
            ORDER BY g.genre_id
            """;
    private static final String GET_BY_ID_GENRE = """
            SELECT g.genre_id, g.genre_type
            FROM genre g
            WHERE g.genre_id = ?
            """;
    private static final String GET_GENRE_OF_FILM = """
            SELECT m.*
            FROM movies m
            LEFT JOIN movie_genre mg ON m.id = mg.movie_id
            WHERE mg.genre_id = ?
            """;
    private static final String CHECK_GENRES = """
            SELECT genre_type
            FROM genre AS mg
            WHERE genre_type IN (?)
            """;

    public GenreFilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<GenreFilm> getAll() {
        return jdbcTemplate.query(GET_ALL_GENRE, (rs, rowNum) ->
                new GenreFilm(
                        rs.getLong("genre_id"),
                        rs.getString("genre_type")
                ));
    }

    @Override
    public GenreFilm getById(Long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID_GENRE, (rs, rowNum) ->
                    new GenreFilm(
                            rs.getLong("genre_id"),
                            rs.getString("genre_type")
                    ), id);
        } catch (EmptyResultDataAccessException ex) {
            log.error("Жанр фильма с id %s не найден!".formatted(id));
            throw new NotFoundException("Жанр фильма с id %s не найден!".formatted(id));
        }
    }

    @Override
    public List<Film> getGenresOfFilm(Long id) {
        return jdbcTemplate.query(GET_GENRE_OF_FILM, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setUserLikes(film.getUserLikes());
            film.setMpa(new Mpa(rs.getLong("rating_id"), rs.getString("rating_name")));
            film.setGenres(parseGenres(rs.getString("genre_ids")));
            return film;
        }, id);
    }

    private Set<GenreFilm> parseGenres(String genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(genreIds.split(","))
                .map(Long::parseLong)
                .map(GenreFilm::new)
                .collect(Collectors.toSet());
    }

    public boolean checkGenres(Set<GenreFilm> genres) {
        List<String> genreNames = genres.stream()
                .map(genre -> {
                    GenreFilm byId = getById(genre.getId());
                    if (genre.getName() == null) {
                        genre.setName(byId.getName());
                    }
                    return genre.getName();
                })
                .toList();

        if (genreNames.isEmpty()) {
            return true;
        }

        String inClause = String.join(", ", Collections.nCopies(genreNames.size(), "?"));

        List<String> genreNamesFromDb = jdbcTemplate.query(
                "SELECT genre_type FROM genre WHERE genre_type IN (" + inClause + ")",
                genreNames.toArray(),
                (rs, rowNum) -> rs.getString("genre_type")
        );

        for (GenreFilm genre : genres) {
            if (!genreNamesFromDb.contains(genre.getName())) {
                throw new NotFoundException("Жанр с именем %s не найден!".formatted(genre.getName()));
            }
        }

        return true;
    }


}
