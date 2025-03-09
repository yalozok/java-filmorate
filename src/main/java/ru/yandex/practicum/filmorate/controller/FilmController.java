package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Validator;

import java.util.*;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private Long id = 0L;
    Map<Long, Film> films = new HashMap<>();

    private Long getNextId() {
        return ++id;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Get films: {} - Started and Finished", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@RequestBody @Validated({Default.class, Validator.Create.class}) Film film) {
        log.info("Create film: {} - Started", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Create film: {} - Finished", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Validated({Default.class, Validator.Update.class}) Film film) {
        log.info("Update film: {} - Started", film);
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id: " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Update film: {} - Finished", film);
        return film;
    }
}
