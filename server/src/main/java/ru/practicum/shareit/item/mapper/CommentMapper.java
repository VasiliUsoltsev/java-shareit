package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {
    public static Comment mapToComment(NewCommentRequest newCommentRequest) {
        if (newCommentRequest == null) {
            return null;
        }

        Comment comment = new Comment();

        comment.setText(newCommentRequest.getText());

        return comment;
    }

    public static CommentResponse mapToCommentResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponse commentResponse = new CommentResponse();

        commentResponse.setId(comment.getId());
        commentResponse.setText(comment.getText());

        if (comment.getAuthor() != null) {
            commentResponse.setAuthorName(comment.getAuthor().getName());
        }

        commentResponse.setCreated(comment.getCreated());

        return commentResponse;
    }
}
