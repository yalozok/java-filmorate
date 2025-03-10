package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {
    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.usingContext().getValidator();
    }

    Film film;

    private static Stream<String> generateDescription() {
        return Stream.of("-".repeat(199), "-".repeat(200));
    }

    private static Stream<LocalDate> generateWrongFutureDate() {
        return Stream.of(LocalDate.now(), LocalDate.now().plusDays(1));
    }

    private static Stream<LocalDate> generateWrongPastDate() {
        return Stream.of(LocalDate.of(1895, 12, 27),
                LocalDate.of(1895, 12, 28));
    }

    private static Stream<LocalDate> generateValidReleaseDate() {
        return Stream.of(LocalDate.of(1895, 12, 29),
                LocalDate.now().minusDays(1));
    }

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("film name");
        film.setReleaseDate(LocalDate.now().minusDays(1));
        film.setDuration(100);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    void validateName_BlankString_ThrowException(String name) {
        film.setName(name);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void validateName_ValidName_Success() {
        film.setName("valid name");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
    }

    @Test
    void validateDescription_TooLongString_ThrowException() {
        film.setDescription("-".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Size.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("generateDescription")
    void validateDescription_ValidString_Success(String description) {
        film.setDescription(description);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
    }

    @ParameterizedTest
    @MethodSource("generateWrongFutureDate")
    void validateDate_ViolateFutureDate_ThrowException(LocalDate releaseDate) {
        film.setReleaseDate(releaseDate);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Past.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("generateWrongPastDate")
    void validateDate_ViolatePastDate_ThrowException(LocalDate releaseDate) {
        film.setReleaseDate(releaseDate);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(AssertTrue.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("validReleaseDate", violation.getPropertyPath().toString());
    }

    @ParameterizedTest
    @MethodSource("generateValidReleaseDate")
    void validateDescription_ValidReleaseDate_Success(LocalDate releaseDate) {
        film.setReleaseDate(releaseDate);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void validateDuration_ViolateNumber_ThrowException(int duration) {
        film.setDuration(duration);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Positive.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    void validateDuration_ValidNumber_Success() {
        film.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
    }

}
