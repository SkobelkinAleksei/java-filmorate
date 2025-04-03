package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_MOVIES = "SELECT * FROM movies";
    private static final String FIND_MOVIE_BY_ID = "SELECT * FROM movies WHERE id = ?";
    private static final String INSERT_MOVIE = """
            INSERT INTO movies (name, description, releaseDate, duration, genre_id, rating_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_MOVIE = """
            UPDATE movies
            SET name = ?, description = ?, releaseDate = ?, duration = ?, genre_id = ?, rating_id = ?
            WHERE id = ?
            """;
    private static final String CHECK_GENRE_EXISTS = "SELECT id FROM movie_genre WHERE id = ?";
    private static final String CHECK_RATING_EXISTS = "SELECT id FROM movie_rating WHERE id = ?";
    private static final String FIND_TOP_MOVIES = """
            SELECT f.*
            FROM movies f
            LEFT JOIN user_likes fl ON f.id = fl.movie_id
            GROUP BY f.id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT 5
            """;
    private static final String ADD_LIKE = "INSERT INTO user_likes (user_id, movie_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM user_likes WHERE user_id = ? AND movie_id = ?";
    private static final String FIND_GENRE_BY_ID = "SELECT id, genre_name FROM movie_genre WHERE id = ?";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM movie_rating WHERE id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(FIND_ALL_MOVIES, (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getObject("releaseDate", LocalDate.class),
                rs.getInt("duration"),
                null,
                getGenreFilm(rs.getLong("genre_id")),
                getRatingFilm(rs.getInt("rating_id"))
        ));
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        return jdbcTemplate.update(ADD_LIKE, userId, filmId) > 0;
    }

    @Override
    public boolean removeLike(long filmId, long userId) {
        return jdbcTemplate.update(REMOVE_LIKE, userId, filmId) > 0;
    }

    @Override
    public List<Film> getTopMovies() {
        return jdbcTemplate.query(FIND_TOP_MOVIES, (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getObject("releaseDate", LocalDate.class),
                rs.getInt("duration"),
                null,
                getGenreFilm(rs.getLong("genre_id")),
                getRatingFilm(rs.getInt("rating_id"))
        ));
    }

    @Override
    public Film getFilm(long filmId) {
        List<Film> films = jdbcTemplate.query(FIND_MOVIE_BY_ID, (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getObject("releaseDate", LocalDate.class),
                rs.getInt("duration"),
                null,
                getGenreFilm(rs.getLong("genre_id")),
                getRatingFilm(rs.getInt("rating_id"))
        ), filmId);

        // Проверяем, найден ли фильм
        if (films.isEmpty()) {
            return null; // Или выбросьте исключение, если хотите
        }

        return films.getFirst();
    }


    @Override
    public Film createFilm(Film film) {
        log.info("Film info before creating: {}", film);

        Integer genreId = Optional.ofNullable(film.getGenre())
                .map(GenreFilm::getId)
                .orElseThrow(() -> new ValidationException("Жанр не найден"));

        Integer ratingId = Optional.ofNullable(film.getRating())
                .map(RatingFilm::getId)
                .orElseThrow(() -> new ValidationException("Рейтинг не найден"));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate().toString());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, genreId);
            ps.setInt(6, ratingId);
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        int updatedRows = jdbcTemplate.update(UPDATE_MOVIE,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getGenre().getId(),
                newFilm.getRating().getId(),
                newFilm.getId());

        if (updatedRows > 0) {
            return getFilm(newFilm.getId());
        } else {
            throw new IllegalArgumentException("Фильм с id %s не найден".formatted(newFilm.getId()));
        }
    }

    private RatingFilm getRatingFilm(int ratingId) {
        return jdbcTemplate.queryForObject(FIND_RATING_BY_ID, (rs, rowNum) ->
                new RatingFilm(
                        rs.getInt("id"),
                        rs.getString("rating")
                ), ratingId);
    }

    private GenreFilm getGenreFilm(long genreId) {
        return jdbcTemplate.queryForObject(FIND_GENRE_BY_ID, (rs, rowNumber) ->
                new GenreFilm(
                        rs.getInt("id"),
                        rs.getString("genre_name")
                ), genreId
        );
    }
}


