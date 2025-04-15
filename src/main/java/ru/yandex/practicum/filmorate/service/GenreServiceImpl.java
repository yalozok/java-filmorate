package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> getGenresType() {
        return genreRepository.getGenresType();
    }

    @Override
    public Genre getById(int id) {
        return genreRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Genre was not found with id: " + id));
    }
}
