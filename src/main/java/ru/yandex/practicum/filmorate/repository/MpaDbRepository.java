package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbRepository implements MpaRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Mpa> getById(final long id) {
        String sql = "SELECT * FROM MPA WHERE MPA.MPA_ID = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        List<Mpa> result = jdbc.query(sql, params,
                (rs, rowNum) -> new Mpa(rs.getLong("MPA_ID"), rs.getString("NAME")));
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    @Override
    public List<Mpa> getMpaType() {
        String sql = "SELECT * FROM MPA ORDER BY MPA_ID";
        return jdbc.query(sql, rs -> {
            LinkedList<Mpa> genres = new LinkedList<>();
            while (rs.next()) {
                genres.add(new Mpa(rs.getLong("MPA_ID"), rs.getString("NAME")));
            }
            return genres;
        });
    }
}
