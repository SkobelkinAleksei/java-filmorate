package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.storage.user.StatusFriend;

import java.time.LocalDate;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@Data
public class User {
    private Long id;
    private String name;

    @NotBlank(message = "Почта не может быть пуста")
    @Email(message = "Почта должна содержать @")
    private String email;

    @NotBlank(message = "Логин не может содержать пробелы")
    @NotNull(message = "Логин не может быть пуст")
    private String login;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Др не может быть пустым или null")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Map<Long, StatusFriend> friendIds;

    public User() {
    }

    public User(Long id, String login, String name, String email, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }

    public User(Long id, String name, String email, String login, LocalDate birthday, Map<Long, StatusFriend> friendIds) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.friendIds = friendIds;
    }
}
