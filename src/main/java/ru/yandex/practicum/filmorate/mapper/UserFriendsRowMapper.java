package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.modelUser.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class UserFriendsRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getObject("birthday", LocalDate.class));
    }
}
