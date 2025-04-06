package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "TestUser",
                "test@example.com",
                "testlogin",
                LocalDate.of(1990, 1, 1),
                new ArrayList<>()
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
        User userId = null;
        try {
            userId = userDbStorage.getUser(999L);
        } catch (NoSuchElementException e) {
            assertThat(userId).isNull();
        }
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
        User existingUser = new User(
                1L,
                "existinglogin",
                "ExistingUser ",
                "existing@example.com",
                LocalDate.of(1990, 1, 1),
                new ArrayList<>()
        );

        userDbStorage.create(existingUser);
        User originalUser = userDbStorage.getUser(1L);

        User userToUpdate = new User(
                999L,
                "nonexistentlogin",
                "NonExistent",
                "nonexistent@example.com",
                LocalDate.of(1990, 1, 1),
                new ArrayList<>()
        );

        userDbStorage.update(userToUpdate);
        User updatedUser = userDbStorage.getUser(1L);

        assertEquals(originalUser.getLogin(), updatedUser.getLogin());
        assertEquals(originalUser.getName(), updatedUser.getName());
        assertEquals(originalUser.getEmail(), updatedUser.getEmail());
        assertEquals(originalUser.getBirthday(), updatedUser.getBirthday());
    }


    @Test
    void testAddFriend() {
        User friend = new User(
                2L,
                "friendlogin",
                "FriendUser",
                "friend@example.com",
                LocalDate.of(1990, 2, 2),
                new ArrayList<>()
        );
        User userFriend = userDbStorage.create(user);
        long userId1 = userFriend.getId();
        User userNewFriend = userDbStorage.create(friend);
        long userId2 = userNewFriend.getId();

        userDbStorage.addFriend(userId1, userId2);

        List<User> friends = userDbStorage.getFriends(userId1);
        assertThat(friends).contains(userNewFriend);

        List<User> friendsOfUser2 = userDbStorage.getFriends(userId2);
        assertThat(friendsOfUser2).doesNotContain(userFriend);
    }

    @Test
    void testRemoveFriend() {
        User userCreate2 = new User(
                2L,
                "friendlogin",
                "FriendUser",
                "friend@example.com",
                LocalDate.of(1990, 2, 2),
                new ArrayList<>()
        );
        User userFriend = userDbStorage.create(user);
        long userId1 = userFriend.getId();
        User userNewFriend = userDbStorage.create(userCreate2);
        long userId2 = userNewFriend.getId();

        userDbStorage.addFriend(userId1, userId2);
        userDbStorage.removeFriend(userId1, userId2);
        List<User> friends = userDbStorage.getFriends(userId1);

        assertThat(friends).doesNotContain(userNewFriend);
    }

    @Test
    void testGetFriends() {
        User userCreate2 = new User(
                2L,
                "friendlogin",
                "FriendUser",
                "friend@example.com",
                LocalDate.of(1990, 2, 2),
                new ArrayList<>()
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
                LocalDate.of(1990, 2, 2),
                new ArrayList<>()
        );

        User userCreate3 = new User(
                3L,
                "friendlogin2",
                "FriendUser2",
                "friend2@example.com",
                LocalDate.of(1990, 3, 3),
                new ArrayList<>()
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
