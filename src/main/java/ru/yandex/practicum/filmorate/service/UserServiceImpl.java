package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User get(long id) {
        return userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + id));
    }

    @Override
    public User add(User user) {
        if (userRepository.isDuplicateEmail(user.getEmail())) {
            throw new ValidationException("Email is already in use " + user.getEmail());
        }
        return userRepository.add(user);
    }

    @Override
    public User update(User user) {
        User oldUser = userRepository.getById(user.getId())
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + user.getId()));

        if (!oldUser.getEmail().equals(user.getEmail()) && userRepository.isDuplicateEmail(user.getEmail())) {
            throw new ValidationException("Email is already in use " + user.getEmail());
        }

        if (user.getBirthday() == null) {
            user.setBirthday(oldUser.getBirthday());
        }
        return userRepository.update(user);
    }

    @Override
    public void delete(long id) {
        userRepository.getById(id).orElseThrow(() -> new NotFoundException("User was not found with id: " + id));
        userRepository.delete(id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + userId));
        userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User as friend was not found with id: " + friendId));
        friendRepository.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("User was not found with id: " + userId));
        userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("User as friend was not found with id: " + friendId));
        friendRepository.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        userRepository.getById(userId).orElseThrow(() -> new NotFoundException("User was not found with id: " + userId));
        return friendRepository.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        List<User> userFriends = friendRepository.getFriends(userId);
        List<User> friendFriends = friendRepository.getFriends(otherId);
        return userFriends.stream().filter(friendFriends::contains).collect(Collectors.toList());
    }
}
