package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();

    Optional<User> get(long id);

    User add(User user);

    boolean isDuplicateEmail(String email);

    User update(User user);

    void delete(long id);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    Optional<List<User>> getFriends(long id);
}
