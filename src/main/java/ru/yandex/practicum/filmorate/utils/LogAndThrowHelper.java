package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogAndThrowHelper implements LogAndThrowMethods{
    @Override
    public void logAndThrow(RuntimeException exception) {
        log.error(exception.getMessage());
        throw exception;
    }
}
