package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreFilmRowMapper;

import java.util.List;

@Repository
public class GenreFilmDbStorage implements GenreFilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreFilmRowMapper genreFilmRowMapper;

    private static final String GET_ALL_GENRE = """
            SELECT *
            FROM movie_genre
            """;
    private static final String GET_BY_ID_GENRE = """
            SELECT *
            FROM movie_genre
            WHERE id = ?
            """;
    private static final String GET_GENRE_OF_FILM = """
            SELECT *
            FROM movie_genre
            WHERE id IN (SELECT genre_id
                         FROM movies
                         WHERE id = ?)
            """;

    public GenreFilmDbStorage(JdbcTemplate jdbcTemplate, GenreFilmRowMapper genreFilmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreFilmRowMapper = genreFilmRowMapper;
    }

    @Override
    public List<GenreFilm> getAll() {
        return jdbcTemplate.query(GET_ALL_GENRE, genreFilmRowMapper);
    }

    @Override
    public GenreFilm getById(long id) {
        return (GenreFilm) jdbcTemplate.query(GET_BY_ID_GENRE, (rs, rowNum) ->
                new GenreFilm(
                        rs.getInt("id"),
                        rs.getString("genre_name")
                ), id);
    }

    @Override
    public List<GenreFilm> getGenresOfFilm(long id) {
        try {
            return jdbcTemplate.query(GET_GENRE_OF_FILM, genreFilmRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
