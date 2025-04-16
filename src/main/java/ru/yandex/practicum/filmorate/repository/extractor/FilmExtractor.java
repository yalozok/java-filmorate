package ru.yandex.practicum.filmorate.repository.extractor;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

@Component
public class FilmExtractor implements ResultSetExtractor<Film> {
    @Override
    public Film extractData(ResultSet rs) throws SQLException {
        Film film = null;
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();

        while (rs.next()) {
            if (film == null) {
                film = new Film();
                film.setId(rs.getLong("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setDuration(rs.getInt("duration"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());

                Mpa mpa = new Mpa(
                        rs.getLong("mpa_id"),
                        rs.getString("mpa_name")
                );
                film.setMpa(mpa);
            }
            long genreId = rs.getLong("genre_id");
            String genreName = rs.getString("genre_name");
            if (genreId > 0 && genreName != null) {
                genres.add(new Genre(genreId, genreName));
            }
        }
        if (film != null) {
            film.setGenres(genres);
        }
        return film;
    }
}
