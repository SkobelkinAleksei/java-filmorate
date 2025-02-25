package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.utils.LogAndThrowHelper;
import ru.yandex.practicum.filmorate.utils.UserValidHelper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController userController;
    private static User user1;
    private static User user2;
    private static User user3;
    private static User user4;
    private static User user5;
    private static User user6;
    private static User user8;
    private static User user9;
    private static User user10;
    private static User user11;
    private static User user12;

    @BeforeAll
    public static void start() throws ValidationException, DuplicateException {
        // Экземпляр сервиса
        UserService userService = new UserService(new InMemoryUserStorage(new LogAndThrowHelper(), new UserValidHelper(new LogAndThrowHelper())));
        // Создаем контроллер, передавая ему сервис и хранилище
        userController = new UserController(userService);

        // Тут все ок
        user1 = User.builder()
                .id(0L)
                .name("name1")
                .email("name1@mail.ru")
                .login("name1")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();
        userController.create(user1);

        user2 = User.builder()
                .id(0L)
                .name("name2")
                .email("name2@mail.ru")
                .login("name2")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();

        // Неправильная почта
        user3 = User.builder()
                .id(0L)
                .name("name3")
                .email("name3mail.ru")
                .login("name3")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();

        // Задублировал почту
        user4 = User.builder()
                .id(0L)
                .name("name4")
                .email("name1@mail.ru")
                .login("name4")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();

        // Неправильный логин
        user5 = User.builder()
                .id(0L)
                .name("name5")
                .email("name5@mail.ru")
                .login(" ")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();

        // Неправильная дата ДР
        user6 = User.builder()
                .id(0L)
                .name("name6")
                .email("name6@mail.ru")
                .login("name6")
                .birthday(LocalDate.of(2028, 05, 19))
                .friendIds(new HashSet<>())
                .build();

        // Такого ID нету
        user8 = User.builder()
                .id(11L)
                .name("name8")
                .email("name8@mail.ru")
                .login("name8")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();

        // storage
        user9 = User.builder()
                .id(0L)
                .name("name9")
                .email("name9@mail.ru")
                .login("name9")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();
        userController.create(user9);

        user10 = User.builder()
                .id(0L)
                .name("name10")
                .email("name10@mail.ru")
                .login("name10")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();
        userController.create(user10);

        user11 = User.builder()
                .id(0L)
                .name("name11")
                .email("name11@mail.ru")
                .login("name11")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();
        userController.create(user11);

        user12 = User.builder()
                .id(0L)
                .name("name12")
                .email("name12@mail.ru")
                .login("name12")
                .birthday(LocalDate.of(2020, 05, 19))
                .friendIds(new HashSet<>())
                .build();
        userController.create(user12);
    }

    @Test
    public void createdUserTest() throws ValidationException, DuplicateException {
        assertEquals(userController.create(user2), user2);
    }

    @Test
    public void invalidEmailTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user3);
        });
        assertNotNull(exception);
    }

    @Test
    public void duplicateEmailTest() {
        DuplicateException exception = assertThrows(DuplicateException.class, () -> {
            userController.create(user4);
        });
        assertNotNull(exception);
    }

    @Test
    public void invalidLoginTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user5);
        });
        assertNotNull(exception);
    }

    @Test
    public void invalidBirthdayTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.create(user6);
        });
        assertNotNull(exception);
    }

    @Test
    public void notHaveIdTest() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userController.update(user8);
        });
        assertNotNull(exception);
    }

    @Test
    public void addFriendTest() {
        // Добавили в друзья
        userController.addFriend(user1.getId(), user9.getId());

        // Проверка, что теперь user9 в друзьях у user1
        assertTrue(user1.getFriendIds().contains(user9.getId()), "User1 должен иметь в друзьях User9");

        // Проверка, что user1 в друзьях у user9
        assertTrue(user9.getFriendIds().contains(user1.getId()), "User9 должен иметь в друзьях User1");
    }

    @Test
    public void removeFriendTest() {
        // Добавили в друзья
        userController.addFriend(user1.getId(), user9.getId());

        // Удалили из друзей
        userController.removeFriend(user1.getId(), user9.getId());

        // Проверка, что теперь user9 в друзьях у user1
        assertFalse(user1.getFriendIds().contains(user9.getId()), "User1 не должен иметь в друзьях User9");

        // Проверка, что user1 в друзьях у user9
        assertFalse(user9.getFriendIds().contains(user1.getId()), "User9 не должен иметь в друзьях User1");
    }

    @Test
    public void getFriendsTest() {
        // Добавили в друзья
        userController.addFriend(user1.getId(), user9.getId());
        // Проверка, что метод getFriends работает правильно.
        Set<User> friendsOfUser = userController.getFriends(user1.getId());
        // Проверка, что user1 действительно имеет user9 в списке друзей
        assertTrue(friendsOfUser.contains(user9), "user1 действительно имеет user9 в списке друзей");
    }

//    @Test
//    public void getMutualFriendsTest() {
//        // Это общие друзья
//        userController.addFriend(user1.getId(), user10.getId());
//        userController.addFriend(user1.getId(), user11.getId());
//
//        // Это общие друзья
//        userController.addFriend(user9.getId(), user10.getId());
//        userController.addFriend(user9.getId(), user11.getId());
//
//        // Это не общий друг
//        userController.addFriend(user9.getId(), user12.getId());
//
//        Set<Long> mutualFriends = userController.getMutualFriends(user1.ge);
//
//        assertTrue(mutualFriends.contains(user10), "User10 должен быть общим другом");
//        assertTrue(mutualFriends.contains(user11), "User11 должен быть общим другом");
//        assertFalse(mutualFriends.contains(user12), "User12 не должен быть общим другом");
//    }

    @AfterAll
    public static void allUsersTest() {
        assertNotNull(userController.findAll());
    }
}
