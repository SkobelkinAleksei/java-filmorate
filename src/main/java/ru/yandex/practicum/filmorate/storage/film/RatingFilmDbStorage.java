package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.RatingFilmRowMapper;

import java.util.Collection;


@Repository
public class RatingFilmDbStorage implements RatingFilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RatingFilmRowMapper ratingFilmRowMapper;

    public RatingFilmDbStorage(JdbcTemplate jdbcTemplate, RatingFilmRowMapper ratingFilmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingFilmRowMapper = ratingFilmRowMapper;
    }

    @Override
    public Collection<RatingFilm> getAll() {
        String sql = """
                SELECT *
                FROM movie_rating
                """;
        return jdbcTemplate.query(sql, ratingFilmRowMapper);
    }

    @Override
    public String getRating(Integer id) {
        String sql = """
                SELECT genre_name
                FROM movie_genre
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, ratingFilmRowMapper, id).toString();
    }

}
