package ru.yandex.practicum.filmorate.storage.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.modelUser.User;
import ru.yandex.practicum.filmorate.model.modelUser.UserCreate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String ADD_FRIEND = """
            INSERT INTO friends(user_id, friend_id, created_at)
            VALUES(?, ?, NOW())
            """;
    private static final String INSERT_USER = """
            INSERT INTO users (name, email, login, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String CHECK_EMAIL_EXISTS = "SELECT (COUNT(*) > 0) FROM users WHERE email = ?";
    private static final String UPDATE_USER = """
            UPDATE users
            SET name = ?, email = ?, login = ?, birthday = ?
            WHERE id = ?
            """;
    private static final String FIND_FRIENDS = """
            SELECT u.*
            FROM friends AS f
            JOIN users AS u ON u.id = f.friend_id
            WHERE f.user_id = ?
            """;
    private static final String FIND_MUTUAL_FRIENDS = """
            SELECT u.*
            FROM friends AS f1
            JOIN friends AS f2 ON f1.friend_id = f2.friend_id
            JOIN users AS u ON u.id = f1.friend_id
            WHERE f1.user_id = ?
            AND f2.user_id = ?
            """;

    private static final String IS_USER_EXISTS_FRIENDS = "SELECT (COUNT(*) > 0) FROM friends AS f WHERE user_id = ? AND friend_id = ?";

    @Autowired
    public UserDbStorage(
            JdbcTemplate jdbcTemplate

    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
      return  jdbcTemplate.query(FIND_ALL_USERS, (rs, rowNum) -> new User(
             rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getObject("birthday", LocalDate.class),
                new HashMap<>()
        ));
    }

    @Override
    public Optional<User> getUser(long userId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_USER_BY_ID, (rs, rowNum) -> new User(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getObject("birthday", LocalDate.class),
                    new HashMap<>()
            ), userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int create(User user) {
        return 0;
    }

    public int create(UserCreate user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getBirthday().toString());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void duplMailCheck(User user) {
        Boolean isExist = jdbcTemplate.queryForObject(CHECK_EMAIL_EXISTS, Boolean.class, user.getEmail());
        if (isExist) {
            throw new IllegalArgumentException("User with email = %s already exists".formatted(user.getEmail()));
        }
    }

    @Override
    public User update(User newUser) {
        log.info("Данные для обновления пользователя = %s".formatted(newUser));
        jdbcTemplate.update(UPDATE_USER, newUser.getName(), newUser.getEmail(), newUser.getLogin(), newUser.getBirthday(), newUser.getId());

        return getUser(newUser.getId()).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id = %s не найден!".formatted(newUser.getId()))
        );
    }

    @Override
    public List<User> getFriends(long userId) {
        return jdbcTemplate.query(FIND_FRIENDS, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getObject("birthday", LocalDate.class),
                new HashMap<>()
        ), userId);
    }

    public boolean isUserExistFriend(long userId, long friendId) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(IS_USER_EXISTS_FRIENDS, Boolean.class, userId, friendId));
    }

    public void isCorrectUser(Long userId) {
        String sql = """
                SELECT (COUNT(*) > 0)
                 FROM users
                 WHERE users.id = ?
                """;
        Boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, userId);

        if (Boolean.FALSE.equals(isExist)) {
            throw new IllegalArgumentException("Такого User не существует %s".formatted(userId));
        }
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
        return true;
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        isUserExistFriend(userId, friendId);
        String sql = "DELETE FROM friends " +
                "WHERE user_id = ? " +
                "AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        return true;
    }

    @Override
    public List<User> getMutualFriends(Long user1, Long user2) {
        return jdbcTemplate.query(FIND_MUTUAL_FRIENDS, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getObject("birthday", LocalDate.class),
                new HashMap<>()
        ), user1, user2);
    }

    private Set<Long> getUserLikes(Long filmId) {
        String sql = "SELECT user_id FROM user_likes WHERE movie_id = ?";
        return new HashSet<>(jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        rs.getLong("user_id"), filmId)
        );
    }
}



























