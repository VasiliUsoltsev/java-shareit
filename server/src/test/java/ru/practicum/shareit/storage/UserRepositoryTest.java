package ru.practicum.shareit.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("Анна Смирнова");
        user1.setEmail("anna.smirnova@mail.ru");

        user2 = new User();
        user2.setName("Иван Петров");
        user2.setEmail("ivan.petrov@mail.ru");
    }

    @Test
    void save_shouldSaveUser() {
        User saved = userRepository.save(user1);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Анна Смирнова");
        assertThat(saved.getEmail()).isEqualTo("anna.smirnova@mail.ru");
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        User saved = userRepository.save(user1);

        Optional<User> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getName()).isEqualTo("Анна Смирнова");
        assertThat(found.get().getEmail()).isEqualTo("anna.smirnova@mail.ru");
    }

    @Test
    void findById_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> found = userRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("Анна Смирнова", "Иван Петров");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoUsers() {
        List<User> users = userRepository.findAll();

        assertThat(users).isEmpty();
    }

    @Test
    void existsById_shouldReturnTrue_whenUserExists() {
        User saved = userRepository.save(user1);

        boolean exists = userRepository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenUserDoesNotExist() {
        boolean exists = userRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    void update_shouldUpdateUserFields() {
        User saved = userRepository.save(user1);

        saved.setName("Анна Иванова");
        saved.setEmail("anna.ivanova@mail.ru");
        User updated = userRepository.save(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("Анна Иванова");
        assertThat(updated.getEmail()).isEqualTo("anna.ivanova@mail.ru");
    }

    @Test
    void delete_shouldDeleteUser() {
        User saved = userRepository.save(user1);

        userRepository.delete(saved);

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldDeleteUser() {
        User saved = userRepository.save(user1);

        userRepository.deleteById(saved.getId());

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectCount() {
        userRepository.save(user1);
        userRepository.save(user2);

        long count = userRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void count_shouldReturnZero_whenNoUsers() {
        long count = userRepository.count();

        assertThat(count).isZero();
    }

    @Test
    void shouldThrowException_whenSaveUserWithDuplicateEmail() {
        userRepository.save(user1);

        User duplicate = new User();
        duplicate.setName("Петр Сидоров");
        duplicate.setEmail("anna.smirnova@mail.ru");

        assertThatThrownBy(() -> userRepository.save(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldThrowException_whenSaveUserWithNullName() {
        User invalid = new User();
        invalid.setEmail("test@mail.ru");

        assertThatThrownBy(() -> userRepository.save(invalid))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldThrowException_whenSaveUserWithNullEmail() {
        User invalid = new User();
        invalid.setName("Тест Тестов");

        assertThatThrownBy(() -> userRepository.save(invalid))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}