package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateUserPatchRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateWhenEmailIsValid() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("anna.ivanova@mail.ru");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenBothFieldsAreSet() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Анна Иванова");
        request.setEmail("anna.ivanova@mail.ru");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail(null);

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email  не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email  не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("   ");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email  не может быть пустым");
    }

    @Test
    void shouldFailWhenEmailIsInvalidWithoutAt() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("annamail.ru");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя некорректный");
    }

    @Test
    void shouldFailWhenEmailIsInvalidWithoutDomain() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("anna@");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя некорректный");
    }

    @Test
    void shouldFailWhenEmailIsInvalidWithoutName() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("@mail.ru");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("email");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Email пользователя некорректный");
    }

    @Test
    void shouldValidateWhenNameIsEmpty() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("");
        request.setEmail("anna.ivanova@mail.ru");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenNameIsNull() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName(null);
        request.setEmail("anna.ivanova@mail.ru");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenNameIsBlank() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("   ");
        request.setEmail("anna.ivanova@mail.ru");

        Set<ConstraintViolation<UpdateUserPatchRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}