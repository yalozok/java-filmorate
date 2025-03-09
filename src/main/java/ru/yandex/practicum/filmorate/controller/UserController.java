package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private Long id = 0L;
    Map<Long, User> users = new HashMap<>();

    private Long getNextId() {
        return ++id;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Get user: {} - Started and Finished", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Validated({Validator.Create.class, Default.class}) User user) {
        log.info("Create user: {} - Started", user);
        if (isDuplicateEmail(user.getEmail())) {
            log.error("Пользователь с email: {} уже существует", user.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Пользователь с email " + user.getEmail() + " уже существует");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Create user: {} - Finished", user);
        return user;
    }

    private boolean isDuplicateEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @PutMapping
    public User updateUser(@RequestBody @Validated({Validator.Update.class, Default.class}) User user) {
        log.info("Update user: {} - Started", user);
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id: " + user.getId() + " не найден");
        }

        User oldUser = users.get(user.getId());
        if (!oldUser.getEmail().equals(user.getEmail()) && isDuplicateEmail(user.getEmail())) {
            log.error("Пользователь с email {} уже существует", user.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Пользователь с email " + user.getEmail() + " уже существует");
        }

        if (user.getBirthday() == null) {
            user.setBirthday(oldUser.getBirthday());
        }
        users.put(user.getId(), user);
        log.info("Update user: {} - Finished", user);
        return user;
    }
}
