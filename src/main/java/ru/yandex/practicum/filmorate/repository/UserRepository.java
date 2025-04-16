package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    Optional<User> getById(long id);

    User add(User user);

    boolean isDuplicateEmail(String email);

    User update(User user);

    void delete(long id);
}
