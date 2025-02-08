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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController userController;
    private static User user1;
    private static User user2;
    private static User user3;
    private static User user4;
    private static User user5;
    private static User user6;
    private static User user7;
    private static User user8;

    @BeforeAll
    public static void start() throws ValidationException, DuplicateException {
        userController = new UserController(new UserService());

        // Тут все ок
        user1 = User.of(0L, "name1", "name1@mail.ru",
                "name111@mail", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userController.create(user1);

        user2 = User.of(0L, "name2", "name2@mail.ru",
                "name2", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Неправильная почта
        user3 = User.of(0L, "name3", "name3mail.ru",
                "name3", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Задублировал почту
        user4 = User.of(0L, "name4", "name1@mail.ru",
                "name4", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Неправильный логин
        user5 = User.of(0L, "name5", "name5@mail.ru",
                " ", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Неправильная дата ДР
        user6 = User.of(0L, "name6", "name6@mail.ru",
                "name6", LocalDate.parse("2028-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Нет ID
        user7 = User.of(null, "name7", "name7@mail.ru",
                "name7", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Такого ID нету
        user8 = User.of(11L, "name8", "name8@mail.ru",
                "name8", LocalDate.parse("2020-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    }

    @Test
    public void сreatedUserTest() throws ValidationException, DuplicateException {
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
    public void noIdTest() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.update(user7);
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
