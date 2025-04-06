package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreFilmDbStorage genreFilmDbStorage;

    private static final String FIND_ALL_MOVIES = """
            SELECT f.*, m.id AS mpa_id, m.mpa
            FROM movies f
            LEFT JOIN movie_rating AS m ON f.mpa_id = m.id;
            """;
    private static final String FIND_MOVIE_BY_ID = """
             SELECT f.id,
                   f.name,
                   f.description,
                   f.releaseDate,
                   f.duration,
                   GROUP_CONCAT(DISTINCT g.genre_id) AS genre_ids,
                   GROUP_CONCAT(DISTINCT g.genre_type) AS genre_names,
                   r.id AS rating_id,
                   r.mpa AS rating_name,
                   mg.movie_id, mg.genre_id,
                   g.genre_id, g.genre_type,
                   GROUP_CONCAT(DISTINCT l.user_id) AS like_ids
            FROM movies AS f
                     LEFT JOIN movie_genre AS mg ON f.id = mg.movie_id
                     LEFT JOIN genre AS g ON mg.genre_id = g.genre_id
                     LEFT JOIN movie_rating AS r ON f.mpa_id = r.id
                     LEFT JOIN user_likes AS l ON f.id = l.movie_id
            WHERE f.id = ?
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, r.id, r.mpa;
            """;

    private static final String INSERT_MOVIE = """
            INSERT INTO movies (name,
                    description,
                    releaseDate,
                    duration,
                    mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_MOVIE = """
            UPDATE movies m
            SET m.name = ?,
                m.description = ?,
                m.releaseDate = ?,
                m.duration = ?
            WHERE m.id = ?
            """;

    private static final String FIND_TOP_MOVIES = """
             SELECT f.id,
                    f.name,
                    f.description,
                    f.releaseDate,
                    f.duration,
                    GROUP_CONCAT(DISTINCT g.genre_id) AS genre_ids,
                    GROUP_CONCAT(DISTINCT g.genre_type) AS genre_names,
                    r.id AS rating_id,
                    r.mpa AS rating_name,
                    COUNT(l.user_id) AS like_count,
                    GROUP_CONCAT(DISTINCT l.user_id) AS like_ids
             FROM movies AS f
                      LEFT JOIN movie_genre AS mg ON f.id = mg.movie_id
                      LEFT JOIN genre AS g ON mg.genre_id = g.genre_id
                      LEFT JOIN movie_rating AS r ON f.mpa_id = r.id
                      LEFT JOIN user_likes AS l ON f.id = l.movie_id
             GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, r.id, r.mpa
             HAVING COUNT(l.user_id) > 0
             ORDER BY like_count DESC;
            """;
    private static final String ADD_LIKE = "INSERT INTO user_likes (user_id, movie_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM user_likes WHERE user_id = ? AND movie_id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreFilmDbStorage genreFilmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreFilmDbStorage = genreFilmDbStorage;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(FIND_ALL_MOVIES, new FilmMapper());
        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setUserLikes(getLikesByFilmId(film.getId()));
        }
        return films;
    }

    private List<Long> getLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM user_likes WHERE movie_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    private Set<GenreFilm> getGenresByFilmId(Long filmId) {
        String sql = """
        SELECT g.genre_id, g.genre_type
        FROM genre g
        JOIN movie_genre mg ON g.genre_id = mg.genre_id
        WHERE mg.movie_id = ?
        ORDER BY g.genre_id ASC;
        """;
        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                new GenreFilm(rs.getLong("genre_id"), rs.getString("genre_type")), filmId));
    }

    public class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            new ArrayList<>();
            film.setMpa(new Mpa(rs.getLong("movie_rating.id"), rs.getString("movie_rating.mpa")));
            return film;
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update(ADD_LIKE, userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(REMOVE_LIKE, userId, filmId);
    }

    @Override
    public List<Film> getTopMovies() {
        return jdbcTemplate.query(FIND_TOP_MOVIES,new FilmMapper());
    }

    private Set<GenreFilm> parseGenres(String genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Set.of();
        }

        return Arrays.stream(genreIds.split(","))
                .map(Long::parseLong)
                .map(genreFilmDbStorage::getById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Film> getFilm(Long filmId) {
        return jdbcTemplate.query(FIND_MOVIE_BY_ID, (rs, rowNum) -> {
            return new Film(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("releaseDate").toLocalDate(),
                    rs.getInt("duration"),
                    new ArrayList<>(),
                    parseGenres(rs.getString("genre_ids")),
                    new Mpa(rs.getLong("rating_id"), rs.getString("rating_name"))
            );
        },filmId).stream().findFirst();
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenres(film.getId(), film.getGenres());
        }

        return film;
    }

    private void addGenres(Long filmId, Set<GenreFilm> genres) {
        log.debug("addGenres({}, {})", filmId, genres);

        Set<GenreFilm> uniqueGenres = new HashSet<>();

        for (GenreFilm genre : genres) {
            if (uniqueGenres.add(genre)) {
                jdbcTemplate.update("INSERT INTO movie_genre (movie_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
                log.trace("Genre {} was added to movie {}", genre.getName(), filmId);
            } else {
                log.trace("Duplicate genre {} found in input and will not be added", genre.getName());
            }
        }
    }

    @Override
    public Film update(Film newFilm) {
        int updatedRows = jdbcTemplate.update(UPDATE_MOVIE,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getId()
        );

        if (updatedRows > 0) {
            return newFilm;
        } else {
            throw new IllegalArgumentException("Фильм с id %s не найден".formatted(newFilm.getId()));
        }
    }
}


