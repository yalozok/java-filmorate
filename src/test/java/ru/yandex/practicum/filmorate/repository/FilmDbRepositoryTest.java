package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.extractor.FilmExtractor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({FilmDbRepository.class, FilmExtractor.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class FilmDbRepositoryTest {
    private final FilmDbRepository filmDbRepository;

    private Film getFilm() {
        Film film = new Film();
        film.setName("test_film");
        film.setDescription("test_description");
        film.setDuration(200);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setMpa(new Mpa(1, "G"));
        return film;
    }

    @Test
    void getAll_ListFilms() {
        List<Film> films = filmDbRepository.getAll();
        assertThat(films).hasSize(6);
    }

    @Test
    void getById_Film() {
        Optional<Film> filmOptional = filmDbRepository.getById(1);
        assertThat(filmOptional).isPresent().hasValueSatisfying(
                film -> assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
        );
    }

    @Test
    void add_FilmAdded() {
        Film film = getFilm();
        Film filmAdded = filmDbRepository.add(film);
        assertThat(filmAdded).hasFieldOrPropertyWithValue("id", 7L);
    }

    @Test
    void update_FilmUpdated() {
        Film film = getFilm();
        Film filmAdded = filmDbRepository.add(film);
        filmAdded.setName("updated_name_film");
        filmDbRepository.update(filmAdded);
        Optional<Film> filmUpdated = filmDbRepository.getById(filmAdded.getId());
        assertThat(filmUpdated).isPresent()
                .hasValueSatisfying(
                        filmUpdatedCheck -> assertThat(filmUpdatedCheck)
                                .hasFieldOrPropertyWithValue("name", "updated_name_film"));
    }

    @Test
    void remove_FilmRemoved() {
        filmDbRepository.delete(1);
        Optional<Film> filmRemoved = filmDbRepository.getById(1);
        assertThat(filmRemoved).isEmpty();
    }

    @Test
    void getPopular_ListFilms() {
        List<Film> topFilms = filmDbRepository.getPopular(5);
        assertThat(topFilms).hasSize(4);
        assertThat(topFilms.getFirst()).hasFieldOrPropertyWithValue("id", 4L);
    }

    @Test
    void addLike_FilmUpInPopular() {
        filmDbRepository.addLike(1, 4);
        List<Film> topFilms = filmDbRepository.getPopular(5);
        assertThat(topFilms.getFirst()).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void removeLike_FilmDownInPopular() {
        filmDbRepository.removeLike(4, 4);
        List<Film> topFilms = filmDbRepository.getPopular(5);
        assertThat(topFilms.getFirst()).hasFieldOrPropertyWithValue("id", 1L);
    }
}
