package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({FriendDbRepository.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class FriendDbRepositoryTest {
    private final FriendDbRepository friendDbRepository;

    @Test
    void getFriend_userList() {
        List<User> friends = friendDbRepository.getFriends(1);
        assertThat(friends).hasSize(2);
    }

    @Test
    void addFriend_userListPlusOneFriend() {
        List<User> friendsBefore = friendDbRepository.getFriends(1);
        assertThat(friendsBefore).hasSize(2);
        friendDbRepository.addFriend(1, 4);
        List<User> friendsAfter = friendDbRepository.getFriends(1);
        assertThat(friendsAfter).hasSize(3);
    }

    @Test
    void removeFriend_userListMinusOneFriend() {
        List<User> friendsBefore = friendDbRepository.getFriends(1);
        assertThat(friendsBefore).hasSize(2);
        friendDbRepository.removeFriend(1, 2);
        List<User> friendsAfter = friendDbRepository.getFriends(1);
        assertThat(friendsAfter).hasSize(1);
    }

    @Test
    void addFriend_OneDirectionalFriendship() {
        List<User> friendsUser2 = friendDbRepository.getFriends(2);
        assertThat(friendsUser2).hasSize(1);

        List<User> friendsUser4 = friendDbRepository.getFriends(4);
        assertThat(friendsUser4).hasSize(0);

        friendDbRepository.addFriend(4, 2);
        List<User> friendsUser4Updated = friendDbRepository.getFriends(4);
        assertThat(friendsUser4Updated).hasSize(1);
        List<User> friendsUser2Updated = friendDbRepository.getFriends(2);
        assertThat(friendsUser2Updated).hasSize(1);
    }
}
