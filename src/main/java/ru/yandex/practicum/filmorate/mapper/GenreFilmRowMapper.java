package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreFilmRowMapper implements RowMapper<GenreFilm> {
    @Override
    public GenreFilm mapRow(ResultSet rs, int rowNum) throws SQLException {
        GenreFilm genre = new GenreFilm();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("name"));

        return genre;
    }
}