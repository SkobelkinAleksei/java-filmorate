package ru.yandex.practicum.filmorate.storage.film;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.modelFilm.Film;
import ru.yandex.practicum.filmorate.model.modelFilm.FilmCreate;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.modelUser.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM movies";
        List<Film> movieList = jdbcTemplate.query(sql, filmRowMapper);
        return movieList.size() < 2 ? List.of(movieList.getFirst()) : movieList;
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        User user = userDbStorage.getUser(userId).orElseThrow(() -> new EntityNotFoundException("User with id %s not found".formatted(userId)));
        String sql = "INSERT INTO user_likes (user_id, movie_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, user.getId(), filmId) > 0;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM user_likes WHERE user_id = ? AND movie_id = ?";
        return jdbcTemplate.update(sql, userId, filmId) > 0;
    }

    @Override
    public List<Film> getTopMovies() {
        String sql = """
                SELECT f.*
                FROM movies f
                LEFT JOIN user_likes fl ON f.id = fl.movie_id
                GROUP BY f.id
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT 5
                """;
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Optional<Film> getFilm(Long filmId) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, filmId);
        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }

    @Override
    public int createFilm(FilmCreate film) {
        log.info("Film info before creating %s".formatted(film.toString()));
        // Проверяем существование жанра
        String sqlCheckGenre = "SELECT id FROM movie_genre WHERE id = ?";
        Integer genreId = jdbcTemplate.query(sqlCheckGenre, (rs, rowNum) -> rs.getInt("id"), film.getGenreId())
                .stream().findFirst().orElseThrow(() -> new ValidationException("Жанр не найден"));

        // Проверяем существование рейтинга
        String sqlCheckRating = "SELECT id FROM movie_rating WHERE id = ?";
        Integer ratingId = jdbcTemplate.query(sqlCheckRating, (rs, rowNum) -> rs.getInt("id"), film.getRatingId())
                .stream().findFirst().orElseThrow(() -> new ValidationException("Рейтинг не найден"));

        String sql = """
                INSERT INTO movies (name, description, releaseDate, duration, genre_id, rating_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate().toString());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, genreId);
            ps.setInt(6, ratingId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public Optional<Film> update(Film newFilm) {
        String sql = """
                UPDATE movies
                SET name = ?, description = ?, releaseDate = ?, duration = ?, genre_id = ?, rating_id = ?
                WHERE id = ?
                """;

        int updatedRows = jdbcTemplate.update(sql,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getGenre().getId(),
                newFilm.getRating().getId(),
                newFilm.getId());

        return updatedRows > 0 ? getFilm(newFilm.getId()) : Optional.empty();
    }
}

