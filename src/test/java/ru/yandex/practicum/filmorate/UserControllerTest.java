package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void UserEmailTest() {
        User user = User.builder()
                .name("User  Name")
                .email("invalidEmail") // некорректный email
                .login("userLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Почта не может быть пустой и должна содержать @", exception.getMessage());
    }

    @Test
    void UserLoginTest() {
        User user = User.builder()
                .name("User  Name")
                .email("user@example.com")
                .login("user Login") // логин с пробелом
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пуст и не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void UserBirthdayTest() {
        User user = User.builder()
                .name("User  Name")
                .email("user@example.com")
                .login("userLogin")
                .birthday(LocalDate.now().plusDays(1)) // дата рождения в будущем
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Дата рождения не может быть пустой или дата указана неправильно", exception.getMessage());
    }

    @Test
    void UserNameInLoginTest() {
        User user = User.builder()
                .name(null) // имя пустое
                .email("user@example.com")
                .login("userLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();



        User user1 = User.builder()
                .name("") // имя пустое
                .email("user@example.com")
                .login("userLogin1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userController.create(user); // Метод создаст пользователя и установит имя
        userController.create(user1); // Метод создаст пользователя и установит имя1
        assertEquals("userLogin", user.getName()); // имя должно быть установлено в логин
        assertEquals("userLogin1", user1.getName()); // имя1 должно быть установлено в логин
    }
}
