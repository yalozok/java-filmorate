package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class Genre {
    @Min(value = 1, message = "GENRE ID должно быть от 1 до 6")
    @Max(value = 6, message = "GENRE ID должно быть от 1 до 6")
    private final long id;
    private final String name;
}
