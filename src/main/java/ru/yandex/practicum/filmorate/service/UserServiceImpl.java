package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User get(long id) {
        return userStorage.get(id)
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + id));
    }

    @Override
    public User add(User user) {
        if (userStorage.isDuplicateEmail(user.getEmail())) {
            throw new ValidationException("Email is already in use " + user.getEmail());
        }
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        User oldUser = userStorage.get(user.getId())
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + user.getId()));

        if (!oldUser.getEmail().equals(user.getEmail()) && userStorage.isDuplicateEmail(user.getEmail())) {
            throw new ValidationException("Email is already in use " + user.getEmail());
        }

        if (user.getBirthday() == null) {
            user.setBirthday(oldUser.getBirthday());
        }
        return userStorage.update(user);
    }

    @Override
    public void delete(long id) {
        userStorage.get(id).orElseThrow(() -> new NotFoundException("User was not found with id: " + id));
        userStorage.delete(id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + userId));
        userStorage.get(friendId)
                .orElseThrow(() -> new NotFoundException("User as friend was not found with id: " + friendId));
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + userId));
        userStorage.get(friendId)
                .orElseThrow(() -> new NotFoundException("User as friend was not found with id: " + friendId));
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        userStorage.get(userId).orElseThrow(() -> new NotFoundException("User was not found with id: " + userId));
        return userStorage.getFriends(userId)
                .orElseGet(Collections::emptyList);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        List<User> userFriends = userStorage.getFriends(userId)
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + userId));

        List<User> friendFriends = userStorage.getFriends(otherId)
                .orElseThrow(() -> new NotFoundException("User as friend was not found with id: " + otherId));

        return userFriends.stream().filter(friendFriends::contains).collect(Collectors.toList());
    }
}
