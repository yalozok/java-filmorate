package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Long id = 0L;
    Map<Long, User> users = new HashMap<>();
    Map<Long, Set<User>> userFriends = new HashMap<>();

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
    public void addFriend(User user, User friend) {
        Set<User> uFriends = userFriends.computeIfAbsent(user.getId(), id -> new HashSet<>());
        uFriends.add(friend);
        userFriends.put(user.getId(), uFriends);

        Set<User> fFriends = userFriends.computeIfAbsent(friend.getId(), k -> new HashSet<>());
        fFriends.add(user);
        userFriends.put(friend.getId(), fFriends);
    }

    @Override
    public void removeFriend(User user, User friend) {
        Set<User> uFriends = userFriends.computeIfAbsent(user.getId(), k -> new HashSet<>());
        uFriends.remove(friend);
        userFriends.put(user.getId(), uFriends);

        Set<User> fFriends = userFriends.computeIfAbsent(friend.getId(), k -> new HashSet<>());
        fFriends.remove(user);
        userFriends.put(friend.getId(), fFriends);
    }

    @Override
    public Optional<List<User>> getFriends(long id) {
        return Optional.ofNullable(userFriends.get(id))
                .map(ArrayList::new);
    }
}
