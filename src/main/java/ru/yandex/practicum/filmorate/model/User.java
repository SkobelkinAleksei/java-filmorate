package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
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

    private List<Long> friendIds;



}
