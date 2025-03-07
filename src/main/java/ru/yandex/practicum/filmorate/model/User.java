package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    @Null(groups = Validator.Create.class)
    @NotNull(groups = Validator.Update.class)
    private Long id;

    @Email(message = "Email должен быть корректным")
    private String email;

    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public void setName(String name) {
        this.name = (name == null || name.isEmpty()) ? this.login : name;
    }
}
