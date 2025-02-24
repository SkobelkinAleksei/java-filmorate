package ru.yandex.practicum.filmorate.exception;

public class ExceptionMessages {

    //ValidationException пользователь
    public static final String EMAIL_CANNOT_BE_EMPTY = "Почта не может быть пустой и должна содержать символ @";
    public static final String LOGIN_CANNOT_BE_EMPTY = "Логин не может быть пуст и не должен содержать пробелы";
    public static final String BIRTHDAY_CANNOT_BE_IN_FUTURE = "Дата рождения не может быть в будущем времени";
    public static final String BIRTHDAY_CANNOT_BE_NULL = "Дата рождения не может быть пустой";
    public static final String USER_NOT_FOUND = "Пользователь null";

    //ValidationException фильмы
    public static final String FILM_NAME_CANNOT_BE_EMPTY = "Название фильма должно быть указано";
    public static final String FILM_LENGTH_IS_NEGATIVE = "Продолжительность фильма не может быть меньше 0";
    public static final String FILM_DESCRIPTION_TOO_LONG = "Описание фильма пустое или превышает 200 символов";
    public static final String FILM_RELEASE_DATE_IS_INCORRECT = "Дата релиза фильма не может быть раньше 28.12.1895г.";
    public static final String FILM_ID_CANNOT_BE_NULL = "ID фильма не может быть null";
    public static final String FILM_NOT_FOUND = "Фильм с id не удалось найти";

    //текс для DuplicatedDataException
    public static final String EMAIL_ALREADY_EXISTS = "Этот емейл уже используется";

    //
    public static final String NO_FRIEND = "Не являются друзьями";

}
