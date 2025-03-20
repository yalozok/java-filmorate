package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Null(groups = Validator.Create.class)
    @NotNull(groups = Validator.Update.class)
    private Long id;

    @Email(message = "Email должен быть корректным")
    private String email;

    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    @Setter(AccessLevel.NONE)
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @Builder(builderMethodName = "customBuilder")
    public static User buildUser(String email, String login, String name, LocalDate birthday) {
        return new User(null, email, login, (name == null || name.isEmpty()) ? login : name, birthday);
    }

    public void setName(String name) {
        this.name = (name == null || name.isEmpty()) ? this.login : name;
    }

    @JsonCreator
    public User(@JsonProperty("email") String email,
                @JsonProperty("login") String login,
                @JsonProperty("name") String name,
                @JsonProperty("birthday") LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = (name == null || name.isEmpty()) ? login : name; // Default name logic
        this.birthday = birthday;
    }
}
