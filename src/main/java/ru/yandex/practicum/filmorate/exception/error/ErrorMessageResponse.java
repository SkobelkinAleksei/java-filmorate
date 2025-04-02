package ru.yandex.practicum.filmorate.exception.error;

public record ErrorMessageResponse(
        String message,
        String created_at
) {

}
