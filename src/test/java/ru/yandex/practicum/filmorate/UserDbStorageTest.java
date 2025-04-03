package ru.yandex.practicum.filmorate;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.UserFriendsRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
                null,
                "TestUser",
                "test@example.com",
                "testlogin",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void testCreateUser() {
        int userId = userDbStorage.create(user);

        Optional<User> createdUser = userDbStorage.getUser(userId);
        assertThat(createdUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", (long) userId);
                    assertThat(user).hasFieldOrPropertyWithValue("name", "TestUser");
                });
    }

    @Test
    void testGetUserById() {
        int userId = userDbStorage.create(user);

        Optional<User> userOptional = userDbStorage.getUser(userId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", (long) userId);
                    assertThat(user).hasFieldOrPropertyWithValue("name", "TestUser");
                });
    }

    @Test
    void testGetUserByIdNotFound() {
        Optional<User> userOptional = userDbStorage.getUser(999L);

        assertThat(userOptional).isEmpty();
    }

    @Test
    void testUpdateUser() {
        int userId = userDbStorage.create(user);
        User user = userDbStorage.getUser(userId).get();

        user.setName("UpdatedUser");
        userDbStorage.update(user);

        Optional<User> updatedUserOptional = userDbStorage.getUser(userId);
        assertThat(updatedUserOptional)
                .isPresent()
                .hasValueSatisfying(updatedUser -> {
                    assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "UpdatedUser");
                });
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User(
                999L,
                "NonExistent",
                "nonexistent@example.com",
                "nonexistentlogin",
                LocalDate.of(1990, 1, 1)
        );

        assertThrows(EntityNotFoundException.class, () -> userDbStorage.update(user));
    }

    @Test
    void testAddFriend() {
        User userCreate2 = new User(
                null,
                "FriendUser",
                "friendlogin",
                "friend@example.com",
                LocalDate.of(1990, 2, 2)
        );
        int userId1 = userDbStorage.create(user);
        int userId2 = userDbStorage.create(userCreate2);

        boolean result = userDbStorage.addFriend(userId1, userId2);

        assertThat(result).isTrue();
    }

    @Test
    void testRemoveFriend() {
        User userCreate2 = new User(
                null,
                "FriendUser",
                "friendlogin",
                "friend@example.com",
                LocalDate.of(1990, 2, 2)
        );
        int userId1 = userDbStorage.create(user);
        int userId2 = userDbStorage.create(userCreate2);

        userDbStorage.addFriend(userId1, userId2);
        boolean result = userDbStorage.removeFriend(userId1, userId2);

        assertThat(result).isTrue();
    }

    @Test
    void testGetFriends() {
        User userCreate2 = new User(
                null,
                "FriendUser",
                "friendlogin",
                "friend@example.com",
                LocalDate.of(1990, 2, 2)
        );
        int userId1 = userDbStorage.create(user);
        int userId2 = userDbStorage.create(userCreate2);

        userDbStorage.addFriend(userId1, userId2);

        List<User> friends = userDbStorage.getFriends(userId1);

        assertThat(friends).isNotEmpty();
        assertThat(friends).anyMatch(friend -> friend.getId() == (long) userId2);
    }

    @Test
    void testGetMutualFriends() {
        User userCreate2 = new User(
                null,
                "FriendUser1",
                "friendlogin1",
                "friend1@example.com",
                LocalDate.of(1990, 2, 2)
        );

        User userCreate3 = new User(
                null,
                "FriendUser2",
                "friendlogin2",
                "friend2@example.com",
                LocalDate.of(1990, 3, 3)
        );
        long userId1 = userDbStorage.create(user);
        long userId2 = userDbStorage.create(userCreate2);
        long userId3 = userDbStorage.create(userCreate3);

        userDbStorage.addFriend(userId1, userId2);
        userDbStorage.addFriend(userId3, userId2);

        List<User> mutualFriends = userDbStorage.getMutualFriends(userId1, userId3);

        assertThat(mutualFriends).isNotEmpty();
        assertThat(mutualFriends).anyMatch(friend -> friend.getId() == userId2);
    }
}
