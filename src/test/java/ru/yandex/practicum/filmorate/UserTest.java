package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    private static final Validator validator;
    User user;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.usingContext().getValidator();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLogin("login");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n"})
    void validateLogin_BlankString_ThrowException(String login) {
        user.setLogin(login);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<User> violation = violations.iterator().next();
        Class<?> annotationType = violation.getConstraintDescriptor().getAnnotation().annotationType();
        assertTrue(annotationType == Pattern.class || annotationType == NotBlank.class);
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void validateLogin_LoginWithSpaces_ThrowException() {
        user.setLogin(" login ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может содержать пробелы", violation.getMessage());
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void validateLogin_ValidLogin_Success() {
        user.setLogin("valid-login");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
    }

    @ParameterizedTest
    @ValueSource(strings = {"email-email", "email", "email@", "e", "@", "email email"})
    void validateEmail_NotValidEmail_ThrowException(String email) {
        user.setEmail(email);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Email.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmail_ValidEmail_Success() {
        user.setEmail("valid@email.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
    }

    @Test
    void validateName_Null_NameEqualsLogin() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void validateBirthday_Future_ThrowException() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Violations found: " + violations);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Past.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("birthday", violation.getPropertyPath().toString());
    }

    @Test
    void validateBirthday_ValidBirthday_Success() {
        user.setBirthday(LocalDate.now().minusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Violations found: " + violations);
    }
}
