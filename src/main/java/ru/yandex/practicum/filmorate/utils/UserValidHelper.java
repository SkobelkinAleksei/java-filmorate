package ru.yandex.practicum.filmorate.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class UserValidHelper implements UserValidMethods{
    private final LogAndThrowHelper logHelper;

    @Override
    public void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@") || email.contains(" ")) {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.EMAIL_CANNOT_BE_EMPTY));
        }
    }

    @Override
    public void validateLogin(String login) {
        if (login == null || login.contains(" ") || login.isEmpty()) {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.LOGIN_CANNOT_BE_EMPTY));
        }
    }

    @Override
    public void validateBirthday(LocalDate birthday) {
        if (birthday != null) {
            if (birthday.isAfter(LocalDate.now())) {
                logHelper.logAndThrow(new ValidationException(ExceptionMessages.BIRTHDAY_CANNOT_BE_IN_FUTURE));
            }
        } else {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.BIRTHDAY_CANNOT_BE_NULL));
        }
    }
}
