package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.utils.LogAndThrowHelper;
import ru.yandex.practicum.filmorate.utils.UserValidHelper;

import java.time.LocalDate;

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

    @BeforeAll
    public static void start() throws ValidationException, DuplicateException {
        userController = new UserController(new UserServiceImpl(new LogAndThrowHelper(), new UserValidHelper(new LogAndThrowHelper())));

        // Тут все ок
        user1 = User.builder()
                .id(0L)
                .name("name1")
                .email("name1@mail.ru")
                .login("name1")
                .birthday(LocalDate.of(2020, 05, 19))
                .build();
        userController.create(user1);


        user2 = User.builder()
                .id(0L)
                .name("name2")
                .email("name2@mail.ru")
                .login("name2")
                .birthday(LocalDate.of(2020, 05, 19))
                .build();

        // Неправильная почта
        user3 = User.builder()
                .id(0L)
                .name("name3")
                .email("name3mail.ru")
                .login("name3")
                .birthday(LocalDate.of(2020, 05, 19))
                .build();

        // Задублировал почту
        user4 = User.builder()
                .id(0L)
                .name("name4")
                .email("name1@mail.ru")
                .login("name4")
                .birthday(LocalDate.of(2020, 05, 19))
                .build();

        // Неправильный логин
        user5 = User.builder()
                .id(0L)
                .name("name5")
                .email("name5@mail.ru")
                .login(" ")
                .birthday(LocalDate.of(2020, 05, 19))
                .build();

        // Неправильная дата ДР
        user6 = User.builder()
                .id(0L)
                .name("name6")
                .email("name6@mail.ru")
                .login("name6")
                .birthday(LocalDate.of(2028, 05, 19))
                .build();

        // Такого ID нету
        user8 = User.builder()
                .id(11L)
                .name("name8")
                .email("name8@mail.ru")
                .login("name8")
                .birthday(LocalDate.of(2020, 05, 19))
                .build();
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

    @AfterAll
    public static void allUsersTest() {
        assertNotNull(userController.findAll());
    }
}
