package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserFriendsRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class, UserFriendsRowMapper.class})
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "testlogin",
                "TestUser",
                "test@example.com",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void testCreateUser() {
        User userCreate = userDbStorage.create(user);

        assertThat(userCreate).isNotNull(); // Проверяем, что пользователь не равен null

        // Проверяем свойства созданного пользователя
        assertThat(userCreate).hasFieldOrPropertyWithValue("name", "TestUser");
        assertThat(userCreate).hasFieldOrPropertyWithValue("email", "test@example.com");
        assertThat(userCreate).hasFieldOrPropertyWithValue("login", "testlogin");
        assertThat(userCreate).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));
    }

    @Test
    void testGetUserById() {
        User userCreate = userDbStorage.create(user);

        assertThat(userCreate).isNotNull(); // Проверяем, что пользователь не равен null
        assertThat(userCreate).hasFieldOrPropertyWithValue("id", userCreate.getId());
        assertThat(userCreate).hasFieldOrPropertyWithValue("name", "TestUser");
    }

    @Test
    void testGetUserByIdNotFound() {
        User userOptional = userDbStorage.getUser(999L);

        assertThat(userOptional).isNull();
    }

    @Test
    void testUpdateUser() {
        User userCreate = userDbStorage.create(user);
        long userId = userCreate.getId();

        user.setName("UpdatedUser");
        userDbStorage.update(user);

        User updatedUser = userDbStorage.getUser(userId);
        assertThat(updatedUser)
                .isNotNull() // Проверяем, что обновленный пользователь не равен null
                .hasFieldOrPropertyWithValue("name", "UpdatedUser");
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User(
                999L,
                "nonexistentlogin",
                "NonExistent",
                "nonexistent@example.com",
                LocalDate.of(1990, 1, 1)
        );

        assertThrows(NotFoundException.class, () -> userDbStorage.update(user));
    }

    @Test
    void testAddFriend() {
        User friend = new User(
                2L,
                "friendlogin",
                "FriendUser",
                "friend@example.com",
                LocalDate.of(1990, 2, 2)
        );
        User userFriend = userDbStorage.create(user);
        long userId1 = userFriend.getId();
        User userNewFriend = userDbStorage.create(friend);
        long userId2 = userNewFriend.getId();

        boolean result = userDbStorage.addFriend(userId1, userId2);

        assertThat(result).isTrue();
    }

    @Test
    void testRemoveFriend() {
        User userCreate2 = new User(
                2L,
                "friendlogin",
                "FriendUser",
                "friend@example.com",
                LocalDate.of(1990, 2, 2)
        );
        User userFriend = userDbStorage.create(user);
        long userId1 = userFriend.getId();
        User userNewFriend = userDbStorage.create(userCreate2);
        long userId2 = userNewFriend.getId();

        userDbStorage.addFriend(userId1, userId2);
        boolean result = userDbStorage.removeFriend(userId1, userId2);

        assertThat(result).isTrue();
    }

    @Test
    void testGetFriends() {
        User userCreate2 = new User(
                2L,
                "friendlogin",
                "FriendUser",
                "friend@example.com",
                LocalDate.of(1990, 2, 2)
        );
        User userFriend = userDbStorage.create(user);
        long userId1 = userFriend.getId();
        User userNewFriend = userDbStorage.create(userCreate2);
        long userId2 = userNewFriend.getId();

        userDbStorage.addFriend(userId1, userId2);

        List<User> friends = userDbStorage.getFriends(userId1);

        assertThat(friends).isNotEmpty();
        assertThat(friends).anyMatch(friend -> friend.getId() == userId2);
    }

    @Test
    void testGetMutualFriends() {
        User userCreate2 = new User(
                2L,
                "friendlogin1",
                "FriendUser1",
                "friend1@example.com",
                LocalDate.of(1990, 2, 2)
        );

        User userCreate3 = new User(
                3L,
                "friendlogin2",
                "FriendUser2",
                "friend2@example.com",
                LocalDate.of(1990, 3, 3)
        );
        User userFriend = userDbStorage.create(user);
        long userId1 = userFriend.getId();
        User friend = userDbStorage.create(userCreate2);
        long userId2 = friend.getId();
        User mutualFriend = userDbStorage.create(userCreate3);
        long userId3 = mutualFriend.getId();

        userDbStorage.addFriend(userId1, userId2);
        userDbStorage.addFriend(userId3, userId2);

        List<User> mutualFriends = userDbStorage.getMutualFriends(userId1, userId3);

        assertThat(mutualFriends).isNotEmpty();
        assertThat(mutualFriends).anyMatch(friends -> friends.getId() == userId2);
    }
}
