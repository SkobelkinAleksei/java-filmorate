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
import ru.yandex.practicum.filmorate.mapper.UserFriendsRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;
    private final UserFriendsRowMapper userFriendsRowMapper;

    @Autowired
    public UserDbStorage(
            JdbcTemplate jdbcTemplate,
            UserRowMapper userRowMapper,
            UserFriendsRowMapper userFriendsRowMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
        this.userFriendsRowMapper = userFriendsRowMapper;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> listUser = jdbcTemplate.query(sql, userRowMapper);
        return listUser.size() < 2 ? List.of(listUser.getFirst()) : listUser;
    }

    @Override
    public Optional<User> getUser(long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, userRowMapper, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int create(User user) {
        return 0;
    }

    public int create(UserCreate user) {
        String sql = """
                INSERT INTO users
                (name, email, login, birthday)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getBirthday().toString());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void duplMailCheck(User user) {
        String sql = "SELECT (count(*) > 0) FROM users WHERE email = ?";
        Boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, user.getEmail());

        if (isExist) {
            throw new IllegalArgumentException("User with email = %s already exists".formatted(user.getEmail()));
        }
    }

    @Override
    public User update(User newUser) {
        log.info("Данные для обновления пользователя = %s".formatted(newUser));
        String sql = """
                UPDATE users
                SET name = ?, email = ?, login = ?, birthday = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, newUser.getName(), newUser.getEmail(), newUser.getLogin(), newUser.getBirthday(), newUser.getId());

        return getUser(newUser.getId()).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id = %s не найден!".formatted(newUser.getId()))
        );
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT u.* " +
                "FROM friends AS f " +
                "JOIN users AS u ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, userFriendsRowMapper, userId);
    }

    public boolean isUserExistFriend(long userId, long friendId) {
        String sql = """
                SELECT (COUNT(*) > 0) FROM friends AS f WHERE user_id = ? AND friend_id = ?
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, friendId));
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
        String sql = "INSERT INTO friends(user_id, friend_id, created_at)\n" +
                "VALUES(?, ?, NOW())";
        jdbcTemplate.update(sql, userId, friendId);

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
        String sql = """
               SELECT u.*
               FROM friends AS f1
               JOIN friends AS f2 ON f1.friend_id = f2.friend_id
               JOIN users AS u ON u.id = f1.friend_id
               WHERE f1.user_id = ?
               AND f2.user_id = ?
       """;
        return jdbcTemplate.query(sql, userFriendsRowMapper, user1, user2);
    }
}



























