package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAll();

    Film get(long id);

    Film add(Film film);

    Film update(Film film);

    void delete(long id);

    void addLike(long id, long userId);

    void removeLike(long id, long userId);

    List<Film> getPopular(int count);
}
