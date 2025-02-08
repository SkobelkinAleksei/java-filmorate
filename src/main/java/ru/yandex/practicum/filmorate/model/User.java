package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;

}
