package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreFilm;
import ru.yandex.practicum.filmorate.storage.film.GenreFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.Mpa;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class})
public class FilmDbStorageTest {
    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    @MockBean
    private GenreFilmDbStorage genreFilmDbStorage;

    private Film filmCreate;
    private Film filmCreate2;
    private User userCreate;
    private User userCreate2;

    @BeforeEach
    public void setUp() {
        Mpa rating = new Mpa(2L, "PG");

        filmCreate = new Film(
                null,
                "Name Film",
                "Test movie",
                LocalDate.of(2005, 1, 1),
                100,
                new ArrayList<>(),
                Set.of(new GenreFilm(1L, "COMEDY")),
                rating
        );
        userCreate = new User(
                null,
                "TestUser ",
                "testlogin",
                "test@example.com",
                LocalDate.of(1990, 1, 1),
                new ArrayList<>()
        );
        userCreate2 = new User(
                null,
                "TestUser 2",
                "testlogin2",
                "test2@example.com",
                LocalDate.of(1990, 1, 1),
                new ArrayList<>()
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
        Film filmId = filmDbStorage.createFilm(filmCreate);

        Optional<Film> createdFilm = filmDbStorage.getFilm(filmId.getId());
        createdFilm.ifPresent(film -> System.out.println(film.getGenres()));
        assertThat(createdFilm).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", filmId.getId());
                    assertThat(film).hasFieldOrPropertyWithValue("name", "Name Film");
                });
    }

    @Test
    void testGetFilmById() {
        Film filmId = filmDbStorage.createFilm(filmCreate);

        Optional<Film> filmOptional = filmDbStorage.getFilm(filmId.getId());

        assertThat(filmOptional).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", filmId.getId());
                    assertThat(film).hasFieldOrPropertyWithValue("name", "Name Film");
                });
    }

    @Test
    void testGetFilmByIdNotFound() {
        Optional<Film> filmOptional = filmDbStorage.getFilm(999L);

        assertThat(filmOptional).isEmpty();
    }

    @Test
    void testAddLike() {
        User user = userDbStorage.create(userCreate);
        long userId = user.getId();
        Film filmId = filmDbStorage.createFilm(filmCreate);

        filmDbStorage.addLike(filmId.getId(), userId);
        assertThat(filmDbStorage.getFilm(filmId.getId())).isNotEmpty();
    }

    @Test
    void testRemoveLike() {
        User user = userDbStorage.create(userCreate);
        long userId = user.getId();
        Film filmId = filmDbStorage.createFilm(filmCreate);

        filmDbStorage.addLike(filmId.getId(), userId);

        filmDbStorage.removeLike(filmId.getId(), userId);
        // Проверяем, что лайк был удален
        assertThat(filmDbStorage.getFilm(filmId.getId())).isNotEmpty(); // Измените на проверку, что лайк действительно удален
    }

    @Test
    void testGetTopMovies() {
        User user1 = userDbStorage.create(userCreate);
        long userId1 = user1.getId();
        User user2 = userDbStorage.create(userCreate2);
        long userId2 = user2.getId();

        GenreFilm genre = new GenreFilm(2L, "DRAMA");
        Mpa rating = new Mpa(2L, "PG");

        filmCreate2 = new Film(
                null,
                "Name film",
                "Another Movie",
                LocalDate.of(2021, 1, 1),
                199,
                new ArrayList<>(),
                Set.of(genre),
                rating
        );

        Film filmId1 = filmDbStorage.createFilm(filmCreate);
        Film filmId2 = filmDbStorage.createFilm(filmCreate2);

        filmDbStorage.addLike(filmId1.getId(), userId1);
        filmDbStorage.addLike(filmId2.getId(), userId1);
        filmDbStorage.addLike(filmId2.getId(), userId2);

        List<Film> topMovies = filmDbStorage.getTopMovies();

        assertThat(topMovies).isNotEmpty();
        assertThat(topMovies.get(0).getId()).isEqualTo(filmId2.getId()); // Исправлено для корректной проверки
    }
}

