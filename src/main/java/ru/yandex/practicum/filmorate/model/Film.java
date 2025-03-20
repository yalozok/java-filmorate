package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    @Null(groups = Validator.Create.class)
    @NotNull(groups = Validator.Update.class)
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @Past(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895")
    private boolean isValidReleaseDate() {
        return releaseDate != null && releaseDate.isAfter(CINEMA_BIRTHDAY);
    }

    public Film() {
    }

    @JsonCreator
    public Film(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("releaseDate") LocalDate releaseDate,
            @JsonProperty("duration") int duration
    ) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;

        if (releaseDate != null && releaseDate.isBefore(CINEMA_BIRTHDAY)) {
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28 декабря 1895");
        }
    }
}
