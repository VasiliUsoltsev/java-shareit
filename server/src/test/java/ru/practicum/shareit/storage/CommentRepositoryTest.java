package ru.practicum.shareit.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Анна Смирнова");
        owner.setEmail("anna@mail.ru");
        owner = userRepository.save(owner);

        author = new User();
        author.setName("Иван Петров");
        author.setEmail("ivan@mail.ru");
        author = userRepository.save(author);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void save_shouldSaveComment() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getText()).isEqualTo("Отличная дрель!");
        assertThat(saved.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(saved.getItem().getId()).isEqualTo(item.getId());
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void findById_shouldReturnComment_whenExists() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);

        Comment found = commentRepository.findById(comment.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(comment.getId());
        assertThat(found.getText()).isEqualTo("Отличная дрель!");
        assertThat(found.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(found.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenCommentDoesNotExist() {
        assertThat(commentRepository.findById(999L)).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllComments() {
        Comment comment1 = new Comment();
        comment1.setText("Отличная дрель!");
        comment1.setAuthor(author);
        comment1.setItem(item);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Очень мощная");
        comment2.setAuthor(owner);
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findAll();

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText)
                .containsExactlyInAnyOrder("Отличная дрель!", "Очень мощная");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoComments() {
        List<Comment> comments = commentRepository.findAll();

        assertThat(comments).isEmpty();
    }

    @Test
    void findByItemId_shouldReturnCommentsForItem() {
        Comment comment1 = new Comment();
        comment1.setText("Отличная дрель!");
        comment1.setAuthor(author);
        comment1.setItem(item);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Очень мощная");
        comment2.setAuthor(owner);
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByItemId(item.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText)
                .containsExactlyInAnyOrder("Отличная дрель!", "Очень мощная");
    }

    @Test
    void findByItemId_shouldReturnEmptyList_whenItemHasNoComments() {
        List<Comment> comments = commentRepository.findByItemId(item.getId());

        assertThat(comments).isEmpty();
    }

    @Test
    void findByItemId_shouldReturnEmptyList_whenItemDoesNotExist() {
        List<Comment> comments = commentRepository.findByItemId(999L);

        assertThat(comments).isEmpty();
    }

    @Test
    void delete_shouldDeleteComment() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);

        commentRepository.delete(comment);

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void deleteById_shouldDeleteComment() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);

        commentRepository.deleteById(comment.getId());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectCount() {
        Comment comment1 = new Comment();
        comment1.setText("Отличная дрель!");
        comment1.setAuthor(author);
        comment1.setItem(item);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Очень мощная");
        comment2.setAuthor(owner);
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now());
        commentRepository.save(comment2);

        long count = commentRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_shouldReturnTrue_whenCommentExists() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);

        boolean exists = commentRepository.existsById(comment.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalse_whenCommentDoesNotExist() {
        boolean exists = commentRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    void shouldThrowException_whenSaveCommentWithNullText() {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> commentRepository.save(comment)
        );
    }

    @Test
    void shouldThrowException_whenSaveCommentWithNullAuthor() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> commentRepository.save(comment)
        );
    }

    @Test
    void shouldThrowException_whenSaveCommentWithNullItem() {
        Comment comment = new Comment();
        comment.setText("Отличная дрель!");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> commentRepository.save(comment)
        );
    }
}