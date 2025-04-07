package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class RatingFilmDbStorage implements RatingFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL_RATING = """
                SELECT *
                FROM movie_rating
                """;

    private static final String GET_BY_ID_RATING = """
            SELECT *
            FROM movie_rating
            WHERE id = ?
            """;

    private static final String GET_BY_NAME_RATING = """
            SELECT *
            FROM movie_rating
            WHERE mpa = ?
            LIMIT 1
            """;

    @Autowired
    public RatingFilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query(GET_ALL_RATING,  (rs, rowNum) ->
                new Mpa(
                        rs.getLong("id"),
                        rs.getString("mpa")
                ));
    }


    @Override
    public Mpa getByName(String name) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_NAME_RATING, (rs, rowNum) ->
                    new Mpa(
                            rs.getLong("id"),
                            rs.getString("mpa")
                    ), name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Mpa getById(Long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID_RATING, (rs, rowNum) ->
                    new Mpa(
                            rs.getLong("id"),
                            rs.getString("mpa")
                    ), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }
}
