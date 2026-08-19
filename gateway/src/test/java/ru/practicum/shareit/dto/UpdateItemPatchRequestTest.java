package ru.practicum.shareit.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateItemPatchRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateWhenAllFieldsAreNull() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenNameIsSet() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Обновленная дрель");

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenDescriptionIsSet() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setDescription("Обновленное описание");

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenAvailableIsSet() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setAvailable(false);

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenAllFieldsAreSet() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Обновленная дрель");
        request.setDescription("Обновленное описание");
        request.setAvailable(true);

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenNameIsEmpty() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("");

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenNameIsBlank() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("   ");

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenDescriptionIsEmpty() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setDescription("");

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenDescriptionIsBlank() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setDescription("   ");

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenAvailableIsNull() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setAvailable(null);

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenAvailableIsTrue() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setAvailable(true);

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenAvailableIsFalse() {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setAvailable(false);

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}