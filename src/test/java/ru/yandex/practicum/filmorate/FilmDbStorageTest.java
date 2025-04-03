package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;
import ru.yandex.practicum.filmorate.storage.film.RatingFilm;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, UserDbStorageTest.class})
public class FilmDbStorageTest {
    @Autowired
    private FilmDbStorage filmDbStorage;

    private Film filmCreate;
    private Film filmCreate2;
    private User userCreate;
    private User userCreate2;
    @Autowired
    private UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp() {
        // Инициализация жанра
        GenreFilm genre = new GenreFilm(1, "COMEDY");

        // Инициализация рейтинга
        RatingFilm rating = new RatingFilm(2, "PG");

        filmCreate = new Film(
                null,
                "Name Film",
                "Test movie",
                LocalDate.of(2005, 1, 1),
                100,
                Set.of(),
                genre,
                rating
        );
        userCreate = new User(
                null,
                "TestUser",
                "testlogin",
                "test@example.com",
                LocalDate.of(1990, 1, 1)
        );
        userCreate2 = new User(
                null,
                "TestUser2",
                "testlogin2",
                "test2@example.com",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void testFindAll() {
        filmDbStorage.createFilm(filmCreate);
        List<Film> allMovies = filmDbStorage.findAll();

        assertThat(allMovies).isNotEmpty();
        assertThat(allMovies).anyMatch(film -> film.getName().equals("Name Film"));
    }

    @Test
    void testCreateFilm() {
        int filmId = filmDbStorage.createFilm(filmCreate);

        Optional<Film> createdFilm = filmDbStorage.getFilm((long) filmId);
        createdFilm.ifPresent(film -> System.out.println(film.getGenre().getName()));
        assertThat(createdFilm).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", (long) filmId);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "Name Film");
                });
    }

    @Test
    void testGetFilmById() {
        int filmId = filmDbStorage.createFilm(filmCreate);

        Optional<Film> filmOptional = filmDbStorage.getFilm((long) filmId);

        assertThat(filmOptional).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", (long) filmId);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "Name Film");
                });
    }

    @Test
    void testGetFilmByIdNotFound() {
        Optional<Film> filmOptional = filmDbStorage.getFilm(999L);

        assertThat(filmOptional).isEmpty();
    }

    @Test
    void testUpdateFilm() {
        int filmId = filmDbStorage.createFilm(filmCreate);
        Optional<Film> filmOptional = filmDbStorage.getFilm((long) filmId);
        Film film = filmOptional.get();

        film.setName("Updated Movie");
        film.setDescription("Updated description");
        Optional<Film> updatedFilmOptional = filmDbStorage.update(film);

        assertThat(updatedFilmOptional).isPresent()
                .hasValueSatisfying(updatedFilm -> {
                    assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Updated Movie");
                    assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "Updated description");
                });
    }

    @Test
    void testAddLike() {
        int userId = userDbStorage.create(userCreate);
        int filmId = filmDbStorage.createFilm(filmCreate);

        boolean likeAdded = filmDbStorage.addLike((long) filmId, (long) userId);
        assertThat(likeAdded).isTrue();
    }

    @Test
    void testRemoveLike() {
        long userId = userDbStorage.create(userCreate);
        int filmId = filmDbStorage.createFilm(filmCreate);

        filmDbStorage.addLike((long) filmId, userId);

        boolean likeRemoved = filmDbStorage.removeLike((long) filmId, userId);
        assertThat(likeRemoved).isTrue();
    }

    @Test
    void testGetTopMovies() {
        long userId1 = userDbStorage.create(userCreate);
        long userId2 = userDbStorage.create(userCreate2);

        // Инициализация жанра
        GenreFilm genre = new GenreFilm(2, "DRAMA");
        // Инициализация рейтинга
        RatingFilm rating = new RatingFilm(2, "PG");

        filmCreate2 = new Film(
                null,
                "Name film",
                "Another Movie",
                LocalDate.of(2021, 1, 1),
                199,
                Set.of(),
                genre,
                rating
        );

        int filmId1 = filmDbStorage.createFilm(filmCreate);
        int filmId2 = filmDbStorage.createFilm(filmCreate2);

        filmDbStorage.addLike((long) filmId1, userId1);
        filmDbStorage.addLike((long) filmId2, userId1);
        filmDbStorage.addLike((long) filmId2, userId2);

        List<Film> topMovies = filmDbStorage.getTopMovies();

        assertThat(topMovies).isNotEmpty();
        assertThat(topMovies.getFirst().getId()).isEqualTo(filmId2);
    }
}
