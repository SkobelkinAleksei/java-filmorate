package ru.yandex.practicum.filmorate.model.modelUser;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCreate {
    @Size(min = 5)
    private String name;
    private String login;

    @NotBlank(message = "Почта не может быть пуста")
    @Email(message = "Почта должна содержать @")
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Др не может быть пустым или null")
    @PastOrPresent(message = "Дата не может быть в будущем")
    private LocalDate birthday;

    public UserCreate(
            String name,
            String login,
            String email,
            LocalDate birthday
    ) {
        this.name = (name == null || name.isEmpty()) ? login : name;
        this.login = login;
        this.email = email;
        this.birthday = birthday;
    }
}
