package ru.yandex.practicum.filmorate.exception;

public class UserFriendAlreadyExist extends RuntimeException {
    public UserFriendAlreadyExist() {
    }

    public UserFriendAlreadyExist(String message) {
        super(message);
    }

    public UserFriendAlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }

    public UserFriendAlreadyExist(Throwable cause) {
        super(cause);
    }
}
