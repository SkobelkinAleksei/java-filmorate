package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingFilmRowMapper implements RowMapper<RatingFilm> {

    @Override
    public RatingFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
        RatingFilm ratingFilm = new RatingFilm();
        ratingFilm.setId(rs.getInt("id"));
        ratingFilm.setName(rs.getString("name"));

        return ratingFilm;
    }
}