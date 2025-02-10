package ru.yandex.practicum.filmorate.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class FilmValidHelper implements FilmValidMethods {
    private final LogAndThrowHelper logHelper;

    @Override
    public void validateFilmId(Long id) {
        if (id == null) {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.FILM_ID_CANNOT_BE_NULL));
        }
    }

    @Override
    public void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.FILM_NAME_CANNOT_BE_EMPTY));
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.FILM_DESCRIPTION_TOO_LONG));
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.FILM_RELEASE_DATE_IS_INCORRECT));
        }
        if (film.getDuration() <= 0) {
            logHelper.logAndThrow(new ValidationException(ExceptionMessages.FILM_LENGTH_IS_NEGATIVE));
        }
    }
}
