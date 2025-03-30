package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public FilmServiceImpl(final UserStorage userStorage, final FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film get(long id) {
        return filmStorage.get(id)
                .orElseThrow(() -> new NotFoundException("Film not found with id: " + id));
    }

    @Override
    public Film add(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film update(Film film) {
        filmStorage.get(film.getId()).orElseThrow(() -> new NotFoundException("Film not found with id: " + film.getId()));
        return filmStorage.update(film);
    }

    @Override
    public void delete(long id) {
        filmStorage.get(id).orElseThrow(() -> new NotFoundException("Film not found with id: " + id));
        filmStorage.delete(id);
    }

    public void addLike(long filmId, long userId) {
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User is not found with id:" + userId));

        Film film = filmStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found with id:" + filmId));
        filmStorage.addLike(film, user);
    }

    public void removeLike(long filmId, long userId) {
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User is not found with id:" + userId));

        Film film = filmStorage.get(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found with id:" + filmId));
        filmStorage.removeLike(film, user);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getRating).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
