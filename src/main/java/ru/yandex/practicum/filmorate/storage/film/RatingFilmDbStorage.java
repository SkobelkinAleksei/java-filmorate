package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.RatingFilmRowMapper;

import java.util.List;

@Slf4j
@Repository
public class RatingFilmDbStorage implements RatingFilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RatingFilmRowMapper ratingFilmRowMapper;

    private static final String GET_ALL_RATING = """
                SELECT *
                FROM movie_rating
                """;

    private static final String GET_BY_ID_RATING = """
            SELECT *
            FROM movie_rating
            WHERE id = ?
            """;

    private static final String GET_RATING_OF_FILM = """
            SELECT *
            FROM movie_rating
            WHERE id IN (SELECT rating_id
                         FROM movies
                         WHERE id = ?)
            """;

    @Autowired
    public RatingFilmDbStorage(JdbcTemplate jdbcTemplate, RatingFilmRowMapper ratingFilmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingFilmRowMapper = ratingFilmRowMapper;
    }

    @Override
    public List<RatingFilm> getAll() {
        return jdbcTemplate.query(GET_ALL_RATING, ratingFilmRowMapper);
    }

    public RatingFilm getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID_RATING, ratingFilmRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null; // Возвращаем null, если рейтинг не найден
        }
    }

    @Override
    public RatingFilm getRatingOfFilm(Long id) {
        return jdbcTemplate.queryForObject(GET_RATING_OF_FILM, ratingFilmRowMapper, id);
    }

}
