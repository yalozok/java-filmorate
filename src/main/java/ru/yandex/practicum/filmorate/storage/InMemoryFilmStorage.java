package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Long id = 0L;
    Map<Long, Film> films = new HashMap<>();
    Map<Long, Set<User>> likes = new HashMap<>();

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> get(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film add(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(long id) {
        films.remove(id);
    }

    @Override
    public void addLike(Film film, User user) {
        Set<User> users = likes.computeIfAbsent(film.getId(), k -> new HashSet<>());
        users.add(user);
        likes.put(film.getId(), users);
    }

    @Override
    public void removeLike(Film film, User user) {
        Set<User> users = likes.computeIfAbsent(film.getId(), k -> new HashSet<>());
        users.remove(user);
        likes.put(film.getId(), users);
    }
}
