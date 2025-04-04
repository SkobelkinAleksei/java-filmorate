package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;



@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    private static final String FIND_ALL_MOVIES = """
            SELECT f.id, f.name, f.description, f.releaseDate, f.duration,
                   GROUP_CONCAT(DISTINCT g.id) AS genre_ids,
                   GROUP_CONCAT(DISTINCT g.name) AS genre_names,
                   r.id AS rating_id, r.rating AS rating_name,
                   l.user_id AS like_id
            FROM movies AS f
            LEFT JOIN movie_genre AS mg ON f.id = mg.movie_id
            LEFT JOIN genres AS g ON mg.genre_id = g.id
            LEFT JOIN movie_rating AS r ON f.rating_id = r.id
            LEFT JOIN user_likes AS l ON f.id = l.movie_id
            GROUP BY f.id
            """;
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
            SELECT f.id, f.name, f.description, f.releaseDate, f.duration,
                   GROUP_CONCAT(DISTINCT g.id) AS genre_ids,
                   GROUP_CONCAT(DISTINCT g.genre_name) AS genre_names,
                   r.id AS rating_id, r.rating AS rating_name
            FROM movies f
            LEFT JOIN user_likes fl ON f.id = fl.movie_id
            LEFT JOIN movie_genre g ON f.genre_id = g.id
            LEFT JOIN movie_rating r ON f.rating_id = r.id
            GROUP BY f.id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT 5
            """;
    private static final String FIND_USERS_LIKE = "SELECT user_id FROM user_likes WHERE movie_id = ?";
    private static final String ADD_LIKE = "INSERT INTO user_likes (user_id, movie_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM user_likes WHERE user_id = ? AND movie_id = ?";
    private static final String FIND_GENRE_BY_ID = "SELECT id, genre_name FROM movie_genre WHERE id = ?";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM movie_rating WHERE id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(FIND_ALL_MOVIES, filmRowMapper);
        Set<Film> uniqueFilms = new TreeSet<>(Comparator.comparing(Film::getId));
        uniqueFilms.addAll(films);
        return new ArrayList<>(uniqueFilms);
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
        return jdbcTemplate.query(FIND_TOP_MOVIES, filmRowMapper);
    }

    @Override
    public Optional<Film> getFilm(long filmId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM movies WHERE id = ?",
                    (rs, rowNum) -> new Film(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getObject("releaseDate", LocalDate.class),
                            rs.getInt("duration"),
                            getUserLikes(filmId),
                            List.of(getGenreFilm(rs.getInt("genre_id"))),
                            getRatingFilm(rs.getInt("rating_id"))
                            ), filmId
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Film info before creating: {}", film);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate().toString());
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getGenre());
            ps.setObject(6, film.getRating());
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
                newFilm.getGenre(),
                newFilm.getRating().getId(),
                newFilm.getId());

        if (updatedRows > 0) {
            return getFilm(newFilm.getId()).orElseThrow();
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

    private List<Long> getUserLikes(Long filmId) {
        String sql = "SELECT user_id FROM user_likes WHERE movie_id = ?";
        return new ArrayList<>(jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        rs.getLong("user_id"), filmId)
        );
    }
}


