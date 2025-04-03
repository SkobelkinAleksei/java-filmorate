package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreFilmRowMapper;

import java.util.Collection;
import java.util.List;

@Repository
public class GenreFilmDbStorage implements GenreFilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreFilmRowMapper genreFilmRowMapper;

    public GenreFilmDbStorage(JdbcTemplate jdbcTemplate, GenreFilmRowMapper genreFilmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreFilmRowMapper = genreFilmRowMapper;
    }

    @Override
    public Collection<GenreFilm> getAll() {
        String sql = "SELECT * FROM movie_genre";
        return jdbcTemplate.query(sql, genreFilmRowMapper);
    }

    @Override
    public String getGenre(Integer id) {
        String sql = """
                SELECT name
                FROM movie_genre
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, genreFilmRowMapper, id).toString();
    }

    @Override
    public List<GenreFilm> getGenresOfFilm(Long filmId) {
        String sql = """
            SELECT mg.name
            FROM movies m
            JOIN movie_genre mg ON m.genre_id = mg.id
            WHERE m.id = ?
            """;

        return jdbcTemplate.query(sql, new Object[]{filmId}, (rs, rowNum) -> {
            GenreFilm genreFilm = new GenreFilm();
            genreFilm.setName(rs.getString("name"));
            return genreFilm;
        });
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
}
