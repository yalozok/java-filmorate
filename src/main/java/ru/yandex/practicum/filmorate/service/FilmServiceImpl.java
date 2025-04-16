package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    @Override
    public List<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Film get(long id) {
        return filmRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with id: " + id));
    }

    @Override
    public Film add(Film film) {
        mpaRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Mpa not found with id: " + film.getMpa().getId()));
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            isValidGenreIds(film.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
        }
        return filmRepository.add(film);
    }

    private void isValidGenreIds(List<Long> genreIds) {
        List<Long> validGenreIds = genreRepository.getGenresIds();
        List<Long> invalidGenreIds = genreIds.stream()
                .filter(id -> !validGenreIds.contains(id))
                .toList();

        if (!invalidGenreIds.isEmpty()) {
            throw new NotFoundException("Genres not found with id(s): " + invalidGenreIds);
        }
    }

    @Override
    public Film update(Film film) {
        filmRepository.getById(film.getId())
                .orElseThrow(() -> new NotFoundException("Film not found with id: " + film.getId()));
        mpaRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Mpa not found with id: " + film.getMpa().getId()));
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            isValidGenreIds(film.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
        }
        return filmRepository.update(film);
    }

    @Override
    public void delete(long id) {
        filmRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with id: " + id));
        filmRepository.delete(id);
    }

    public void addLike(long filmId, long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found with id:" + userId));

        Film film = filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found with id:" + filmId));
        filmRepository.addLike(film.getId(), user.getId());
    }

    public void removeLike(long filmId, long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found with id:" + userId));

        Film film = filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found with id:" + filmId));
        filmRepository.removeLike(film.getId(), user.getId());
    }

    public List<Film> getPopular(int count) {
        return filmRepository.getPopular(count);
    }
}
