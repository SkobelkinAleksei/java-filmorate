package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String ADD_FRIEND = """
            INSERT INTO friends(user_id, friend_id, created_at)
            VALUES(?, ?, NOW())
            """;
    private static final String INSERT_USER = """
            INSERT INTO users (login, name, email, birthday)
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
    private static final String IS_CORRECT_USER = """
            SELECT (COUNT(*) > 0)
            FROM users
            WHERE users.id = ?
            """;

    private static final String IS_USER_EXISTS_FRIENDS = "SELECT (COUNT(*) > 0) FROM friends AS f WHERE f.user_id = ? AND f.friend_id = ?";

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
                getFriends(rs.getLong("id")).stream().map(User::getId).toList()
        ));
    }

    @Override
    public User getUser(long userId) {
        try {
            return jdbcTemplate.queryForObject(FIND_USER_BY_ID, (rs, rowNum) -> new User(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getObject("birthday", LocalDate.class),
                    getFriends(rs.getLong("id")).stream().map(User::getId).toList()
            ), userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User create(User user) {
        log.info("Данные для создания пользователя = %s".formatted(user));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin()); // логин
            ps.setString(2, user.getName()); // имя
            ps.setString(3, user.getEmail()); // email
            ps.setString(4, user.getBirthday().toString());// день рождения
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());

        return user;
    }

    @Override
    public User update(User newUser) {
        User user = getUser(newUser.getId());
        jdbcTemplate.update(UPDATE_USER, newUser.getLogin(), newUser.getName(), newUser.getEmail(), newUser.getBirthday(), user.getId());
        user.setName(newUser.getName());
        user.setLogin(newUser.getLogin());
        user.setBirthday(newUser.getBirthday());
        user.setEmail(newUser.getEmail());
        return  user;
    }

    @Override
    public List<User> getFriends(long userId) {
       return jdbcTemplate.query(FIND_FRIENDS, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getObject("birthday", LocalDate.class)
        ), userId);
    }

    public Integer isUserExistFriend(long userId, long friendId) {
        Integer countOfFriends = jdbcTemplate.queryForObject(IS_USER_EXISTS_FRIENDS, Integer.class, userId, friendId);
        return countOfFriends != null ? countOfFriends : 0;
    }

    public void isCorrectUser(Long userId) {
        Integer isExist = jdbcTemplate.queryForObject(IS_CORRECT_USER, Integer.class, userId);

        if (isExist == null || isExist == 0) {
            throw new NotFoundException("Такого User не существует %s".formatted(userId));
        }
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        int update = jdbcTemplate.update(ADD_FRIEND, userId, friendId);
        return update > 0;
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        jdbcTemplate.update(REMOVE_FRIEND, userId, friendId);
        jdbcTemplate.update(REMOVE_FRIEND, friendId, userId);
        return true;
    }

    @Override
    public List<User> getMutualFriends(long user1, long user2) {
        return jdbcTemplate.query(FIND_MUTUAL_FRIENDS, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getObject("birthday", LocalDate.class)
        ), user1, user2);
    }
}



























