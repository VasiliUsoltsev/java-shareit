package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.NewCommentRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NewCommentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateValidComment() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Отличная вещь!");

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateValidCommentWithSpaces() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("  Отличная вещь!  ");

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenTextIsNull() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText(null);

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("text");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Комментарий не может быть пустым");
    }

    @Test
    void shouldFailWhenTextIsEmpty() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("");

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("text");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Комментарий не может быть пустым");
    }

    @Test
    void shouldFailWhenTextIsBlank() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("   ");

        Set<ConstraintViolation<NewCommentRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("text");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Комментарий не может быть пустым");
    }
}