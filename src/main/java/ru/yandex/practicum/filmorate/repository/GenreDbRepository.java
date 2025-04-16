package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbRepository implements GenreRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Genre> getById(final long id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<Genre> genres = jdbc.query(sql, params,
                (rs, rowNum) -> new Genre(rs.getLong("GENRE_ID"), rs.getString("NAME")));
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.getFirst());
    }

    @Override
    public List<Genre> getGenresType() {
        String sql = "SELECT * FROM GENRES ORDER BY GENRE_ID";
        return jdbc.query(sql, rs -> {
            LinkedList<Genre> genres = new LinkedList<>();
            while (rs.next()) {
                genres.add(new Genre(rs.getLong("GENRE_ID"), rs.getString("NAME")));
            }
            return genres;
        });
    }

    @Override
    public List<Long> getGenresIds() {
        String sql = "SELECT GENRE_ID FROM GENRES ORDER BY GENRE_ID";
        return jdbc.query(sql, rs -> {
            LinkedList<Long> genresIds = new LinkedList<>();
            while (rs.next()) {
                genresIds.add(rs.getLong("GENRE_ID"));
            }
            return genresIds;
        });
    }

}
