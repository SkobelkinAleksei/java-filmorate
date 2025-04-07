package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String FIND_ALL_USERS = """
            SELECT u.id,
            u.name,
            u.email,
            u.login,
            u.birthday
            FROM users AS u
            """;
    private static final String FIND_USER_BY_ID = """
            SELECT u.id,
            u.name,
            u.email,
            u.login,
            u.birthday
            FROM users AS u WHERE id = ?
            """;
    private static final String ADD_FRIEND = """
            INSERT INTO friends (user_id, friend_id, created_at)
            VALUES (?, ?, NOW())
            """;
    private static final String UPDATE_USER = """
            UPDATE users
            SET name = ?, email = ?, login = ?, birthday = ?
            WHERE id = ?
            """;
    private static final String CREATE_USER = """
            INSERT INTO users
            (name, email, login, birthday)
            VALUES(?, ?, ?, ?)
            """;

    private static final String FIND_FRIENDS = """
            SELECT u.*
            FROM friends AS f
            JOIN users AS u ON u.id = f.friend_id
            WHERE f.user_id = ?
            """;
    private static final String REMOVE_FRIEND = """
            DELETE FROM friends
            WHERE user_id = ?
            AND friend_id = ?
            """;
    private static final String FIND_MUTUAL_FRIENDS = """
            SELECT u.*
            FROM friends AS f1
            JOIN friends AS f2 ON f1.friend_id = f2.friend_id
            JOIN users AS u ON u.id = f1.friend_id
            WHERE f1.user_id = ?
            AND f2.user_id = ?
            """;

    public List<Long> getFriendsId(Long userId) {
        String sql = """
                SELECT u.*
                FROM friends AS f
                LEFT JOIN users AS u ON f.user_id = u.id
                WHERE f.user_id = ?
                """;
        List<Long> list = jdbcTemplate.query(sql, new DataClassRowMapper<>(User.class), userId).stream()
                .map(User::getId)
                .toList();

        return !list.isEmpty() ? list : Collections.emptyList();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_USERS, (rs, rowNum) ->
                new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getDate("birthday").toLocalDate(),
                        getFriendsId(rs.getLong("id"))
                ));
    }

    @Override
    public User getUser(Long userId) {
        List<User> query = jdbcTemplate.query(FIND_USER_BY_ID, (rs, rowNum) ->
                new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getDate("birthday").toLocalDate(),
                        getFriendsId(rs.getLong("id"))
                ), userId);

        if (query.isEmpty()) {
            throw new NotFoundException("Юзер с тайм id не найден");
        }

        return query.getFirst();
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_USER, new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return user;
    }

    @Override
    public User update(User newUser) {
        int update = jdbcTemplate.update(UPDATE_USER,
                newUser.getName(),
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getBirthday(),
                newUser.getId()
        );

        return newUser;
    }

    @Override
    public List<User> getFriends(Long userId) {
        return jdbcTemplate.query(FIND_FRIENDS, (rs, rowNum) ->
                new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getDate("birthday").toLocalDate(),
                        getFriendsId(rs.getLong("id"))
                ), userId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(REMOVE_FRIEND, userId, friendId);
    }

    @Override
    public List<User> getMutualFriends(Long user1, Long user2) {
        return jdbcTemplate.query(FIND_MUTUAL_FRIENDS, (rs, rowNum) ->
                new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getDate("birthday").toLocalDate(),
                        getFriendsId(rs.getLong("id"))
                ), user1, user2);
    }
}



























