package ru.yandex.practicum.filmorate.utils;

import java.time.LocalDate;

public interface UserValidMethods {
    void validateEmail(String email);

    void validateLogin(String login);

    void validateBirthday(LocalDate birthday);
}
