package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Long id = 0L;
    Map<Long, User> users = new HashMap<>();
    Map<Long, Set<Long>> userFriends = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User add(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    public boolean isDuplicateEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        Set<Long> uFriendIds = userFriends.computeIfAbsent(userId, id -> new HashSet<>());
        uFriendIds.add(friendId);
        userFriends.put(userId, uFriendIds);

        Set<Long> fFriendIds = userFriends.computeIfAbsent(friendId, k -> new HashSet<>());
        fFriendIds.add(userId);
        userFriends.put(friendId, fFriendIds);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        Set<Long> uFriendIds = userFriends.computeIfAbsent(userId, k -> new HashSet<>());
        uFriendIds.remove(friendId);
        userFriends.put(userId, uFriendIds);

        Set<Long> fFriendIds = userFriends.computeIfAbsent(friendId, k -> new HashSet<>());
        fFriendIds.remove(userId);
        userFriends.put(friendId, fFriendIds);
    }

    @Override
    public Optional<List<User>> getFriends(long id) {
        return Optional.ofNullable(userFriends.get(id))
                .map(friends -> friends.stream()
                        .map(users::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
    }
}
