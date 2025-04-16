package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({UserDbRepository.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class UserDbRepositoryTest {
    private final UserRepository userRepository;

    private User getUser() {
        User user = new User();
        user.setLogin("test");
        user.setEmail("test@yandex.ru");
        user.setName("test");
        user.setBirthday(LocalDate.of(1995, 1, 1));
        return user;
    }

    @Test
    void getAll_users() {
        List<User> users = userRepository.getAll();
        assertThat(users).hasSize(4);
    }

    @Test
    void getUserById_user() {
        Optional<User> user = userRepository.getById(1);
        assertThat(user).isPresent()
                .hasValueSatisfying(
                        user1 -> assertThat(user1).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "sveta@email.ru")
                );
    }

    @Test
    void getUserByNotValidId_OptionalEmpty() {
        Optional<User> user = userRepository.getById(99);
        assertThat(user).isEmpty();
    }

    @Test
    void getAdd_userAdded() {
        User user = getUser();
        User savedUser = userRepository.add(user);
        assertThat(savedUser).hasFieldOrPropertyWithValue("id", savedUser.getId());
    }

    @Test
    void isDuplicateEmail_true() {
        Boolean result = userRepository.isDuplicateEmail("sveta@email.ru");
        assertThat(result).isTrue();
    }

    @Test
    void isDuplicateEmail_false() {
        Boolean result = userRepository.isDuplicateEmail("test@yandex.ru");
        assertThat(result).isFalse();
    }

    @Test
    void update_userUpdated() {
        User user = getUser();
        User userToUpdate = userRepository.add(user);
        userToUpdate.setEmail("updatedEmail@yandex.ru");
        userRepository.update(userToUpdate);
        Optional<User> userUpdated = userRepository.getById(userToUpdate.getId());
        assertThat(userUpdated).isPresent()
                .hasValueSatisfying(user1 -> assertThat(user1)
                        .hasFieldOrPropertyWithValue("email", userToUpdate.getEmail()));
    }

    @Test
    void delete_userRemoved() {
        userRepository.delete(1L);
        Optional<User> user = userRepository.getById(1);
        assertThat(user).isEmpty();
    }
}
