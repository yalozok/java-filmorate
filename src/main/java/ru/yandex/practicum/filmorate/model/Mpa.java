package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class Mpa {
    @Min(value = 1, message = "MPA ID должно быть от 1 до 5")
    @Max(value = 5, message = "MPA ID должно быть от 1 до 5")
    private final long id;
    private final String name;
}
