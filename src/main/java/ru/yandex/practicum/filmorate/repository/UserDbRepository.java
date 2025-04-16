package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.extractor.UserExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDbRepository implements UserRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM USERS";
        return jdbc.query(sql, rs -> {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = new UserExtractor().extractData(rs);
                users.add(user);
            }
            return users;
        });
    }

    @Override
    public Optional<User> getById(long id) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource("user_id", id);
        return Optional.ofNullable(jdbc.query(sql, params, rs -> {
            if (rs.next()) {
                return new UserExtractor().extractData(rs);
            }
            return null;
        }));
    }

    @Override
    public User add(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday());

        String sql = """
                INSERT INTO USERS (NAME, EMAIL, LOGIN, BIRTHDAY)
                VALUES (:name, :email, :login, :birthday)
                """;
        jdbc.update(sql, params, keyHolder, new String[]{"user_id"});
        Integer key = (Integer) keyHolder.getKey();
        assert key != null;
        user.setId(key.longValue());
        return user;
    }

    @Override
    public boolean isDuplicateEmail(String email) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE EMAIL = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        Long count = Optional.ofNullable(jdbc.queryForObject(sql, params, Long.class))
                .orElse(0L);
        return count > 0;
    }

    @Override
    public User update(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("birthday", user.getBirthday())
                .addValue("user_id", user.getId());

        String sql = """
                UPDATE USERS SET
                NAME = :name,
                EMAIL = :email,
                LOGIN = :login,
                BIRTHDAY = :birthday
                WHERE USER_ID = :user_id
                """;
        jdbc.update(sql, params);
        return user;
    }

    @Override
    public void delete(long id) {
        String sqlFriends = "DELETE FROM FRIENDS WHERE USER_ID = :user_id OR FRIEND_ID = :user_id";
        String sqlLikes = "DELETE FROM LIKES WHERE USER_ID = :user_id";
        String sqlUsers = "DELETE FROM USERS WHERE USER_ID = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource("user_id", id);
        jdbc.update(sqlLikes, params);
        jdbc.update(sqlFriends, params);
        jdbc.update(sqlUsers, params);
    }
}

