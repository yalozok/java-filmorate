package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Optional<Film> get(long id);

    Film add(Film film);

    Film update(Film film);

    void delete(long id);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);
}
