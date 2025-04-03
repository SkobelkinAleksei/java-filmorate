package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.modelFilm.Film;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


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
                null,
                null,
                null
        );
    }

}
