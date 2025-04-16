package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaRepository repository;

    public List<Mpa> getMpaType() {
        return repository.getMpaType();
    }

    public Mpa getById(long id) {
        return repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Mpa was not found with id " + id));
    }
}
