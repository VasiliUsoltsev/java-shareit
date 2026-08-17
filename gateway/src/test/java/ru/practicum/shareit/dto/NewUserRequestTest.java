package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.NewUserRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NewUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateValidUser() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("anna.smirnova@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateValidUserWithSimpleEmail() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван Петров");
        request.setEmail("ivan@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenNameIsNull() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("anna.smirnova@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Имя пользователя не может быть пустым");
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        NewUserRequest request = new NewUserRequest();
        request.setName("");
        request.setEmail("anna.smirnova@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Имя пользователя не может быть пустым");
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        NewUserRequest request = new NewUserRequest();
        request.setName("   ");
        request.setEmail("anna.smirnova@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Имя пользователя не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("   ");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsInvalidWithoutAt() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("annamail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя некорректный");
    }

    @Test
    void shouldFailWhenEmailIsInvalidWithoutDomain() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("anna@");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя некорректный");
    }

    @Test
    void shouldFailWhenEmailIsInvalidWithoutName() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("@mail.ru");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя некорректный");
    }

    @Test
    void shouldFailWhenAllFieldsAreInvalid() {
        NewUserRequest request = new NewUserRequest();

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name", "email");
    }
}