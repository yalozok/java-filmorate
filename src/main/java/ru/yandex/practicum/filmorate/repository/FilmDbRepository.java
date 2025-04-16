package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.extractor.FilmExtractor;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbRepository implements FilmRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private final FilmExtractor filmExtractor;

    @Override
    public List<Film> getAll() {
        String filmsSql = """
                    SELECT f.*,
                           mpa.MPA_ID as mpa_id, mpa.NAME as mpa_name
                    FROM FILMS f
                    INNER JOIN MPA mpa ON f.MPA_ID = mpa.MPA_ID
                    ORDER BY f.FILM_ID
                """;
        String genresSql = "SELECT * FROM GENRES";
        String genreFilmSql = "SELECT * FROM FILM_GENRE";

        List<Film> films = jdbc.query(filmsSql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("FILM_ID"));
            film.setName(rs.getString("NAME"));
            film.setDescription(rs.getString("DESCRIPTION"));
            film.setDuration(rs.getInt("DURATION"));
            film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
            film.setMpa(new Mpa(rs.getLong("MPA_ID"), rs.getString("MPA_NAME")));
            return film;
        });

        Map<Long, Genre> genresMap = new HashMap<>();
        jdbc.query(genresSql, (rs) -> {
            genresMap.put(rs.getLong("GENRE_ID"),
                    new Genre(rs.getLong("GENRE_ID"), rs.getString("NAME")));
        });

        Map<Long, Set<Long>> genresFilmMap = jdbc.query(genreFilmSql, rs -> {
            Map<Long, Set<Long>> genresFilm = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("FILM_ID");
                long genreId = rs.getLong("GENRE_ID");
                genresFilm.computeIfAbsent(filmId, k -> new HashSet<>()).add(genreId);
            }
            return genresFilm;
        });

        films.forEach(film -> {
            assert genresFilmMap != null;
            if (genresFilmMap.containsKey(film.getId())) {
                LinkedHashSet<Genre> genres = new LinkedHashSet<>();
                Set<Long> genreIds = genresFilmMap.get(film.getId());
                for (Long genreId : genreIds) {
                    genres.add(genresMap.get(genreId));
                }
                film.setGenres(genres);
            }
        });
        return films;
    }

    @Override
    public Optional<Film> getById(long id) {
        String sql = """
                SELECT f.*,
                       mpa.MPA_ID as mpa_id, mpa.NAME as mpa_name,
                       g.GENRE_ID as genre_id, g.NAME as genre_name
                FROM FILMS as f
                LEFT JOIN FILM_GENRE as fg ON f.FILM_ID = fg.FILM_ID
                LEFT JOIN GENRES as g ON g.GENRE_ID = fg.GENRE_ID
                INNER JOIN MPA as mpa ON mpa.MPA_ID = f.MPA_ID
                WHERE f.FILM_ID = :film_id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("film_id", id);
        return Optional.ofNullable(jdbc.query(sql, params, filmExtractor));
    }


    @Override
    public Film add(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId())
                .addValue("release_date", film.getReleaseDate());

        String sql = """
                INSERT INTO FILMS (NAME, DESCRIPTION, DURATION, MPA_ID, RELEASE_DATE)
                VALUES (:name, :description, :duration, :mpa_id, :release_date)
                """;

        jdbc.update(sql, params, keyHolder, new String[]{"film_id"});
        Integer key = (Integer) keyHolder.getKey();
        assert key != null;
        film.setId(key.longValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            updateFilmGenres(film);
        }
        return film;
    }

    private void updateFilmGenres(Film film) {
        List<MapSqlParameterSource> genreList = film.getGenres()
                .stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("genre_id", genre.getId()))
                .toList();

        String insertGenresSql = """
                INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)
                VALUES (:film_id, :genre_id)
                """;
        jdbc.batchUpdate(insertGenresSql, genreList.toArray(new SqlParameterSource[0]));
    }

    @Override
    public Film update(Film film) {
        String updateFilmSql = """
                UPDATE FILMS SET
                        NAME=:name,
                        DESCRIPTION=:description,
                        DURATION=:duration,
                        MPA_ID=:mpa_id,
                        RELEASE_DATE=:release_date
                WHERE FILM_ID = :film_id
                """;
        MapSqlParameterSource paramsFilmUpdate = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId())
                .addValue("release_date", film.getReleaseDate())
                .addValue("film_id", film.getId());
        jdbc.update(updateFilmSql, paramsFilmUpdate);

        MapSqlParameterSource paramsGenresDelete = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            jdbc.update("DELETE FROM FILM_GENRE WHERE FILM_ID = :film_id", paramsGenresDelete);
            updateFilmGenres(film);
        }
        return film;
    }

    @Override
    public void delete(long id) {
        String sqlGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = :film_id";
        String sqlLikes = "DELETE FROM LIKES WHERE FILM_ID = :film_id";
        String sqlFilms = "DELETE FROM FILMS WHERE FILM_ID = :film_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("film_id", id);
        jdbc.update(sqlGenres, params);
        jdbc.update(sqlLikes, params);
        jdbc.update(sqlFilms, params);
    }

    @Override
    public void addLike(long filmId, long userId) {
        Optional<Integer> count = checkLike(filmId, userId);

        if (count.isPresent() && count.get() == 0) {
            String sql = """
                        INSERT INTO LIKES (user_id, film_id)
                        VALUES (:user_id, :film_id)
                    """;
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("user_id", userId)
                    .addValue("film_id", filmId);
            jdbc.update(sql, params);
        }
    }

    private Optional<Integer> checkLike(long filmId, long userId) {
        String sql = """
                SELECT COUNT(*) FROM LIKES
                WHERE FILM_ID = :film_id AND USER_ID = :user_id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("film_id", filmId);
        return Optional.ofNullable(jdbc.queryForObject(sql, params, Integer.class));
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Optional<Integer> count = checkLike(filmId, userId);
        if (count.isPresent() && count.get() > 0) {
            String sql = "DELETE FROM LIKES WHERE FILM_ID = :film_id AND USER_ID = :user_id";
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("film_id", filmId)
                    .addValue("user_id", userId);
            jdbc.update(sql, params);
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlFilms = "SELECT F.*, M.MPA_ID, M.NAME as MPA_NAME FROM FILMS as F " +
                "LEFT JOIN MPA AS M ON F.MPA_ID = M.MPA_ID " +
                "INNER JOIN (" +
                "SELECT FILM_ID, COUNT(USER_ID) as USER_LIKES " +
                "FROM LIKES GROUP BY FILM_ID " +
                "LIMIT " + count +
                ") as TOP_FILMS on F.FILM_ID = TOP_FILMS.FILM_ID " +
                "ORDER BY TOP_FILMS.USER_LIKES DESC, F.FILM_ID";

        String sqlGenres = """
                SELECT FG.FILM_ID, G.GENRE_ID, G.NAME AS genre_name
                FROM FILM_GENRE AS FG
                INNER JOIN GENRES AS G ON FG.GENRE_ID = G.GENRE_ID
                """;

        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbc.query(sqlFilms, rs -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setDuration(rs.getInt("duration"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setMpa(new Mpa(
                    rs.getLong("mpa_id"),
                    rs.getString("mpa_name")
            ));
            film.setGenres(new LinkedHashSet<>());
            filmMap.put(film.getId(), film);
        });

        jdbc.query(sqlGenres, rs -> {
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));

                Film film = filmMap.get(filmId);
                if (film != null) {
                    film.getGenres().add(genre);
                }
            }
        });
        return new ArrayList<>(filmMap.values());
    }
}
