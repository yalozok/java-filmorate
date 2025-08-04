package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendRepository {
    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getFriends(long id);
}
