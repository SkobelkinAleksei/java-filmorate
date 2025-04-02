package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.modelFilm.Film;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film>, Serializable {
    private final JdbcTemplate jdbcTemplate;

    public FilmRowMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        long genreId = rs.getLong("genre_id");
        int ratingId = rs.getInt("rating_id");

        return new Film(
                id,
                rs.getString("name"),
                rs.getString("description"),
                rs.getObject("releaseDate", LocalDate.class),
                rs.getInt("duration"),
                getUserLikes(id),
                getGenreFilm(genreId),
                getRatingFilm(ratingId)

        );
    }

    private Set<Long> getUserLikes(Long filmId) {
        String sql = "SELECT user_id FROM user_likes WHERE movie_id = ?";
        return new HashSet<>(jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        rs.getLong("user_id"), filmId)
        );
    }

    private GenreFilm getGenreFilm(Long genreId) {
        String sql = "SELECT id, genre_name FROM movie_genre WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNumber) ->
            new GenreFilm(
                    rs.getInt("id"),
                    rs.getString("genre_name")
            ), genreId
        );
    }

    private RatingFilm getRatingFilm(int ratingId) {
        String sql = "SELECT * FROM movie_rating WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new RatingFilm(
                        rs.getInt("id"),
                        rs.getString("rating")
                ), ratingId);
    }
}
