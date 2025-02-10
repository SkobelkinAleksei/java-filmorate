package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
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
    private LocalDate birthday;
}
