package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.NewItemRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NewItemRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateValidItem() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateValidItemWithRequestId() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);
        request.setRequestId(1L);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenNameIsNull() {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Название вещи не может быть пустым");
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        NewItemRequest request = new NewItemRequest();
        request.setName("");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Название вещи не может быть пустым");
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        NewItemRequest request = new NewItemRequest();
        request.setName("   ");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Название вещи не может быть пустым");
    }

    @Test
    void shouldFailWhenDescriptionIsNull() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("description");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Описание вещи не может быть пустым");
    }

    @Test
    void shouldFailWhenDescriptionIsEmpty() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("description");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Описание вещи не может быть пустым");
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("   ");
        request.setAvailable(true);

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("description");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Описание вещи не может быть пустым");
    }

    @Test
    void shouldFailWhenAvailableIsNull() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("available");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Доступность вещи не может быть пустой");
    }

    @Test
    void shouldFailWhenAllFieldsAreInvalid() {
        NewItemRequest request = new NewItemRequest();

        Set<ConstraintViolation<NewItemRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("name", "description", "available");
    }
}