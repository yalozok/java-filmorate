package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.extractor.UserExtractor;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendDbRepository implements FriendRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void addFriend(long userId, long friendId) {
        if (!existFriendship(userId, friendId)) {
            String sql = """
                    INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (:user_id, :friend_id)
                    """;
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("user_id", userId)
                    .addValue("friend_id", friendId);
            jdbc.update(sql, params);
        }
    }

    private Boolean existFriendship(long userId, long friendId) {
        String sql = """
                SELECT EXISTS (
                                SELECT 1 FROM FRIENDS
                                WHERE USER_ID = :user_id AND FRIEND_ID = :friend_id
                            )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        return Boolean.TRUE.equals(jdbc.queryForObject(sql, params, Boolean.class));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        if (existFriendship(userId, friendId)) {
            String sql = """
                    DELETE FROM FRIENDS WHERE USER_ID = :user_id AND FRIEND_ID = :friend_id
                    """;
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("user_id", userId)
                    .addValue("friend_id", friendId);
            jdbc.update(sql, params);
        }
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = """
                SELECT * FROM USERS WHERE USER_ID IN (
                    SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = :user_id
                )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return jdbc.query(sql, params, rs -> {
            List<User> friends = new ArrayList<>();
            while (rs.next()) {
                User user = new UserExtractor().extractData(rs);
                if (user != null) {
                    friends.add(user);
                }
            }
            return friends;
        });
    }
}
