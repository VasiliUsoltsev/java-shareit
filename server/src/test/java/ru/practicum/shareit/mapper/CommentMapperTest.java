package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void mapToComment_shouldMapNewCommentRequest() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Отличная вещь!");

        Comment comment = CommentMapper.mapToComment(request);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("Отличная вещь!");
    }

    @Test
    void mapToComment_shouldReturnNull_whenRequestIsNull() {
        Comment comment = CommentMapper.mapToComment(null);

        assertThat(comment).isNull();
    }

    @Test
    void mapToComment_shouldHandleEmptyText() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("");

        Comment comment = CommentMapper.mapToComment(request);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEmpty();
    }

    @Test
    void mapToCommentResponse_shouldMapAllFields() {
        User author = new User();
        author.setId(1L);
        author.setName("Анна Смирнова");

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("Отличная вещь!");
        comment.setCreated(LocalDateTime.of(2026, 8, 17, 12, 0));
        comment.setAuthor(author);

        CommentResponse response = CommentMapper.mapToCommentResponse(comment);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getText()).isEqualTo("Отличная вещь!");
        assertThat(response.getAuthorName()).isEqualTo("Анна Смирнова");
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2026, 8, 17, 12, 0));
    }

    @Test
    void mapToCommentResponse_shouldReturnNull_whenCommentIsNull() {
        CommentResponse response = CommentMapper.mapToCommentResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToCommentResponse_shouldHandleNullAuthor() {
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("Отличная вещь!");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(null);

        CommentResponse response = CommentMapper.mapToCommentResponse(comment);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getText()).isEqualTo("Отличная вещь!");
        assertThat(response.getAuthorName()).isNull();
        assertThat(response.getCreated()).isNotNull();
    }
}